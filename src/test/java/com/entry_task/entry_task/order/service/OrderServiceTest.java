package com.entry_task.entry_task.order.service;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.cart.entity.CartItem;
import com.entry_task.entry_task.cart.service.CartService;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.common.dto.Metadata;
import com.entry_task.entry_task.common.dto.Pagination;
import com.entry_task.entry_task.common.dto.Sort;
import com.entry_task.entry_task.enums.OrderStatus;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.order.dto.*;
import com.entry_task.entry_task.order.entity.Order;
import com.entry_task.entry_task.order.entity.OrderItem;
import com.entry_task.entry_task.order.repository.OrderRepository;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.service.ProductService;
import com.entry_task.entry_task.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.logging.Filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private AuthService authService;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void name() {
    }

    @Test
    void createOrder_validCartItems_shouldReturnOrderResponse() {
        // Constants
        final long CART_ID = 100L;
        final long PRODUCT_ID_1 = 10L;
        final long PRODUCT_ID_2 = 11L;
        final long ORDER_ID = 50L;
        final int PRODUCT_PRICE_1 = 100;
        final int PRODUCT_PRICE_2 = 200;
        final int PRODUCT_STOCK_1 = 5;
        final int PRODUCT_STOCK_2 = 10;
        final int QUANTITY_1 = 2;
        final int QUANTITY_2 = 3;
        final String IDEMPOTENCY_KEY = "test-key-123";

        // Given
        User user = TestEntityFactory.createCustomerWithId("user");

        when(authService.getCurrentUser()).thenReturn(user);

        Product product1 = TestEntityFactory.createProductWithId("Product 1", user);
        product1.setId(PRODUCT_ID_1);
        product1.setPrice(PRODUCT_PRICE_1);
        product1.setStock(PRODUCT_STOCK_1);
        product1.setProductStatus(ProductStatus.ACTIVE);

        Product product2 = TestEntityFactory.createProductWithId("Product 2", user);
        product2.setId(PRODUCT_ID_2);
        product2.setPrice(PRODUCT_PRICE_2);
        product2.setStock(PRODUCT_STOCK_2);
        product2.setProductStatus(ProductStatus.ACTIVE);

        CartItem cartItem1 = TestEntityFactory.createCartItem(TestEntityFactory.createEmptyCart(user), product1, QUANTITY_1);
        cartItem1.setId(1L);
        CartItem cartItem2 = TestEntityFactory.createCartItem(TestEntityFactory.createEmptyCart(user), product2, QUANTITY_2);
        cartItem2.setId(2L);
        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        CreateOrderRequest request = new CreateOrderRequest(
                cartItems.stream().map(CartItem::getId).toList()
        );

        when(cartService.findAllCartItemsByIdAndUser(request.cartItemIds(), user))
                .thenReturn(cartItems);

        Order savedOrder = new Order(user);
        savedOrder.setId(ORDER_ID);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(ORDER_ID);
            return o;
        });

        // When
        OrderResponse response = orderService.createOrder(request, IDEMPOTENCY_KEY);

        // Then
        assertNotNull(response);
        assertEquals(OrderStatus.CREATED.name(), response.status()); // assuming default status
        assertEquals(PRODUCT_PRICE_1 * QUANTITY_1 + PRODUCT_PRICE_2 * QUANTITY_2, response.totalAmount());

        // Verify product stock updated
        verify(productService).reserveStock(PRODUCT_ID_1, QUANTITY_1);
        verify(productService).reserveStock(PRODUCT_ID_2, QUANTITY_2);

        // Verify interactions
        verify(cartService).findAllCartItemsByIdAndUser(request.cartItemIds(), user);
        verify(cartService).deleteCartItems(cartItems);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getUserOrdersList_validRequest_shouldReturnOrderList() {
        // Constants
        final long USER_ID = 1L;
        final int PAGE = 0;
        final int SIZE = 10;
        final long TOTAL_ELEMENTS = 2L;
        final long ORDER_ID_1 = 1001L;
        final long ORDER_ID_2 = 1002L;

        // Create customer
        User customer = TestEntityFactory.createCustomer("customer");
        customer.setId(USER_ID);
        when(authService.getCurrentUser()).thenReturn(customer);

        // Create products
        Product product1 = TestEntityFactory.createProductWithId("Laptop", customer);
        Product product2 = TestEntityFactory.createProductWithId("Phone", customer);

        // Create orders with items
        Order order1 = new Order(customer);
        order1.setId(ORDER_ID_1);
        OrderItem item1 = new OrderItem();
        item1.setProduct(product1);
        item1.setQuantity(2);
        item1.setPrice(500);
        order1.addItem(item1);  // adds item1 to order1.items
        order1.recalculateTotal();

        Order order2 = new Order(customer);
        order2.setId(ORDER_ID_2);
        OrderItem item2 = new OrderItem();
        item2.setProduct(product2);
        item2.setQuantity(1);
        item2.setPrice(300);
        order2.addItem(item2);
        order2.recalculateTotal();

        // Mock repository to return Page<Order>
        Page<Order> orderPage = new PageImpl<>(
                List.of(order1, order2),
                PageRequest.of(PAGE, SIZE),
                TOTAL_ELEMENTS
        );
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(orderPage);

        // Build request
        OrderListRequest request = new OrderListRequest(
                new Pagination(PAGE, SIZE),
                new OrderFilter(List.of(OrderStatus.CREATED.name())),
                new Sort("cTime", "ASC")
        );

        // When
        OrderListResponse response = orderService.getUserOrdersList(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.orderSummaries().size());

        // Verify order summaries are correctly mapped
        OrderSummary summary1 = response.orderSummaries().get(0);
        assertEquals(ORDER_ID_1, summary1.orderId());
        assertEquals(1, summary1.totalItems());
        assertEquals(2, summary1.totalQuantity());
        assertEquals(1000, summary1.totalPrice());
        assertEquals(OrderStatus.CREATED, summary1.status());
        assertEquals(1, summary1.orderItems().size());
        assertEquals(product1.getId(), summary1.orderItems().get(0).productId());

        OrderSummary summary2 = response.orderSummaries().get(1);
        assertEquals(ORDER_ID_2, summary2.orderId());
        assertEquals(1, summary2.totalItems());
        assertEquals(1, summary2.totalQuantity());
        assertEquals(300, summary2.totalPrice());
        assertEquals(OrderStatus.CREATED, summary2.status());
        assertEquals(1, summary2.orderItems().size());
        assertEquals(product2.getId(), summary2.orderItems().get(0).productId());

        // Verify pagination metadata
        Metadata metadata = response.metadata();
        assertEquals(TOTAL_ELEMENTS, metadata.totalItems());
        assertEquals(PAGE, metadata.currentPage());
        assertEquals(SIZE, metadata.pageSize());
        assertFalse(metadata.hasNext());

        // Verify interactions
        verify(authService).getCurrentUser();
        verify(orderRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getSellerOrderInvoiceItemList() {
    }

    @Test
    void getOrderInvoiceItemList() {
    }
}