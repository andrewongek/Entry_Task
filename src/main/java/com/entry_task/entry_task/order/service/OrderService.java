package com.entry_task.entry_task.order.service;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.auth.service.AuthServiceImpl;
import com.entry_task.entry_task.cart.entity.CartItem;
import com.entry_task.entry_task.cart.service.CartService;
import com.entry_task.entry_task.common.dto.Metadata;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.exceptions.InsufficientStockException;
import com.entry_task.entry_task.exceptions.InvalidCartItemException;
import com.entry_task.entry_task.exceptions.ProductNotActiveException;
import com.entry_task.entry_task.exceptions.ProductNotFoundException;
import com.entry_task.entry_task.order.dto.*;
import com.entry_task.entry_task.order.entity.Order;
import com.entry_task.entry_task.order.entity.OrderItem;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.order.repository.OrderItemRepository;
import com.entry_task.entry_task.order.repository.OrderRepository;
import com.entry_task.entry_task.order.specifications.OrderSpecifications;
import com.entry_task.entry_task.product.service.ProductService;
import com.entry_task.entry_task.user.entity.User;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, AuthService authService, CartService cartService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.authService = authService;
        this.cartService = cartService;
        this.productService = productService;
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    @Retryable(retryFor = {OptimisticLockException.class})
    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = authService.getCurrentUser();
        log.info("User {} has initiated a order for cart items {}", user.getId(), request.cartItemIds());
        List<CartItem> cartItems =
                cartService.findAllCartItemsByIdAndUser(
                        request.cartItemIds(), user);

        if (cartItems.size() != request.cartItemIds().size()) {
            throw new InvalidCartItemException("One or more cart items are invalid");
        }

        log.debug("User {} cartItems: {}", user.getId(),
                cartItems.stream()
                        .map(item -> String.format("cartItemId = %d, productId=%d, quantity=%d",
                                item.getCart().getId(),
                                item.getProduct().getId(),
                                item.getQuantity()))
                        .toList());

        Order order = new Order(user);
        List<Product> updatedProducts = new ArrayList<>();

        for (CartItem item : cartItems) {
            Product product = item.getProduct();

            if (!product.getProductStatus().equals(ProductStatus.ACTIVE)) {
                throw new ProductNotActiveException();
            }

            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getId()
                );
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.addItem(orderItem);

            product.setStock(product.getStock() - item.getQuantity());
            updatedProducts.add(product);
        }

        order.recalculateTotal();

        orderRepository.save(order);
        productService.batchUpdate(updatedProducts);
        cartService.deleteCartItems(cartItems);

        log.info("Order {} successfully created by user {}. Total items: {}, Total amount: {}",
                order.getId(), user.getId(), order.getItems().size(), order.getTotalAmount());

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getcTime(),
                order.getmTime()
        );
    }

    @PreAuthorize("hasRole('USER')")
    public OrderListResponse getUserOrdersList(OrderListRequest request) {
        User currentUser = authService.getCurrentUser();
        Page<OrderSummary> page = getUserOrdersList(request, currentUser.getId());
        return new OrderListResponse(
                page.toList(),
                new Metadata(
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

        List<OrderItemResponse> orderItemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
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
                orderItemResponses,
                order.getStatus(),
                order.getcTime(),
                order.getmTime()
        );
    }
}

