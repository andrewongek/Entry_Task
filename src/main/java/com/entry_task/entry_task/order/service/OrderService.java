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
import com.entry_task.entry_task.order.specifications.OrderItemSpecifications;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    private final OrderItemRepository orderItemRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderInvoiceCacheService orderInvoiceCacheService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, AuthService authService, CartService cartService, ProductService productService, OrderInvoiceCacheService orderInvoiceCacheService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.authService = authService;
        this.cartService = cartService;
        this.productService = productService;
        this.orderInvoiceCacheService = orderInvoiceCacheService;
    }

    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
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

    @PreAuthorize("hasRole('CUSTOMER')")
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

    @PreAuthorize("hasRole('SELLER')")
    public OrderInvoiceItemListResponse getSellerOrderInvoiceItemList(SellerOrderInvoiceItemListRequest request) {
        return orderInvoiceCacheService.getSellerOrderInvoiceItemList(request, authService.getCurrentUser().getId());
    }


    @PreAuthorize("hasRole('ADMIN')")
    public OrderInvoiceItemListResponse getOrderInvoiceItemList(AdminOrderInvoiceItemListRequest request) {
        Sort.Direction sortDirection =
                Sort.Direction.fromString(request.sort().order());

        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );

        Specification<OrderItem> spec = Specification.unrestricted();
        if (request.sellerId() != null) {
            return orderInvoiceCacheService.getSellerOrderInvoiceItemList(new SellerOrderInvoiceItemListRequest(request.pagination(), request.filter(), request.sort()), request.sellerId());
        }
        if (request.filter() != null) {
            if (request.filter().keyword() != null && !request.filter().keyword().isBlank()) {
                spec = spec.and(OrderItemSpecifications.productNameContains(request.filter().keyword()));
            }
            if (request.filter().userId() != null) {
                spec = spec.and(OrderItemSpecifications.userIdEquals(request.filter().userId()));
            }
            if (request.filter().orderId() != null) {
                spec = spec.and(OrderItemSpecifications.orderIdEquals(request.filter().orderId()));
            }
            if (request.filter().orderItemId() != null) {
                spec = spec.and(OrderItemSpecifications.orderItemIdEquals(request.filter().orderItemId()));
            }
            if (request.filter().statuses() != null && !request.filter().statuses().isEmpty()) {
                spec = spec.and(OrderItemSpecifications.orderStatusIn(request.filter().statuses()));
            }
        }

        Page<OrderInvoiceItemResponse> page = orderItemRepository.findAll(spec, pageable).map(orderItem -> new OrderInvoiceItemResponse(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getOrder().getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getQuantity() * orderItem.getPrice(),
                orderItem.getOrder().getStatus().name(),
                orderItem.getOrder().getcTime(),
                orderItem.getOrder().getmTime()
        ));

        return new OrderInvoiceItemListResponse(
                page.toList(),
                new Metadata(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );

    }
}

