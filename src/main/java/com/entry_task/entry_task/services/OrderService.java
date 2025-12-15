package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.*;
import com.entry_task.entry_task.enums.OrderStatus;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.model.*;
import com.entry_task.entry_task.repository.OrderItemRepository;
import com.entry_task.entry_task.repository.OrderRepository;
import com.entry_task.entry_task.sql.OrderSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;

    public OrderService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, AuthService authService, CartService cartService) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.authService = authService;
        this.cartService = cartService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = authService.getCurrentUser();

        List<CartItem> cartItems =
                cartService.findAllCartItemsByIdAndUser(
                        request.cartItemIds(), user);

        if (cartItems.size() != request.cartItemIds().size()) {
            throw new IllegalArgumentException("Invalid cart items");
        }

        Order order = new Order(user);

        for (CartItem item : cartItems) {
            Product product = item.getProduct();

            if (!product.getProductStatus().equals(ProductStatus.ACTIVE)) {
                throw new IllegalStateException("Product unavailable");
            }

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.addItem(orderItem);

            product.setStock(product.getStock() - item.getQuantity());
        }

        order.recalculateTotal();
        orderRepository.save(order);

        cartService.deleteCartItems(cartItems);

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getcTime(),
                order.getmTime()
        );
    }

    public OrderListResponse getUserOrdersList(OrderListRequest request) {
        User currentUser = authService.getCurrentUser();
        Page<OrderSummary> page = getUserOrdersList(request, currentUser.getId());
        return new OrderListResponse(
                page.toList(),
                new MetadataDto(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    private Page<OrderSummary> getUserOrdersList(OrderListRequest request, Long userId) {

        Sort.Direction sortDirection =
                Sort.Direction.fromString(request.sort().order());

        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );

        Specification<Order> spec = OrderSpecifications.belongsToUser(userId);

        if (request.filter() != null && request.filter().statuses() != null && !request.filter().statuses().isEmpty()) {
            spec = spec.and(OrderSpecifications.statusIn(request.filter().statuses()));
        }

        return orderRepository.findAll(spec, pageable).map(this::toOrderSummary);
    }

    private OrderSummary toOrderSummary(Order order) {

        int totalItems = order.getItems().size();

        int totalQuantity = order.getItems()
                .stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        List<OrderItemDto> orderItemDtos = order.getItems()
                .stream()
                .map(item -> new OrderItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getQuantity() * item.getPrice()
                ))
                .toList();

        return new OrderSummary(
                order.getId(),
                totalItems,
                totalQuantity,
                order.getTotalAmount(),
                orderItemDtos,
                order.getStatus(),
                order.getcTime(),
                order.getmTime()
        );
    }
}

