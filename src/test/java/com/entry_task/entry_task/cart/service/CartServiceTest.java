package com.entry_task.entry_task.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.cart.dto.CartItemDto;
import com.entry_task.entry_task.cart.dto.CartItemResponse;
import com.entry_task.entry_task.cart.dto.CartResponse;
import com.entry_task.entry_task.cart.dto.UpdateCartRequest;
import com.entry_task.entry_task.cart.entity.Cart;
import com.entry_task.entry_task.cart.entity.CartItem;
import com.entry_task.entry_task.cart.repository.CartItemRepository;
import com.entry_task.entry_task.cart.repository.CartRepository;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.exceptions.CartItemNotFoundException;
import com.entry_task.entry_task.exceptions.CartNotFoundException;
import com.entry_task.entry_task.exceptions.InsufficientStockException;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.service.ProductService;
import com.entry_task.entry_task.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CartServiceTest {

  @InjectMocks private CartService cartService;

  @Mock private AuthService authService;

  @Mock private ProductService productService;

  @Mock private CartRepository cartRepository;

  @Mock private CartItemRepository cartItemRepository;

  @Captor ArgumentCaptor<CartItem> cartItemCaptor;

  @Captor ArgumentCaptor<Cart> cartCaptor;

  @Test
  void getCart_cartItemExist_shouldReturnCartResponse() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product1 = TestEntityFactory.createProduct("Product1", seller);
    product1.setId(1L);
    Product product2 = TestEntityFactory.createProduct("Product2", seller);
    product2.setId(2L);
    Cart cart = TestEntityFactory.createEmptyCart(customer);
    cart.setId(1L);
    CartItem cartItem1 = TestEntityFactory.createCartItem(cart, product1);
    cartItem1.setId(1L);
    CartItem cartItem2 = TestEntityFactory.createCartItem(cart, product2);
    cartItem2.setId(2L);

    CartItemDto expected1 =
        new CartItemDto(
            cartItem1.getId(),
            cartItem1.getQuantity(),
            product1.getId(),
            product1.getName(),
            product1.getPrice(),
            product1.getStock(),
            product1.getSeller().getId(),
            cartItem1.getQuantity() * product1.getPrice(),
            cart.getmTime());

    CartItemDto expected2 =
        new CartItemDto(
            cartItem2.getId(),
            cartItem2.getQuantity(),
            product2.getId(),
            product2.getName(),
            product2.getPrice(),
            product2.getStock(),
            product2.getSeller().getId(),
            cartItem2.getQuantity() * product2.getPrice(),
            cart.getmTime());

    List<CartItemDto> cartItemDtos = List.of(expected1, expected2);

    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(cartItemRepository.findCartItemDtos(customer.getId())).thenReturn(cartItemDtos);
    CartResponse response = cartService.getCart();

    // Then
    assertCartResponse(cartItemDtos, response);
  }

  @Test
  void addProductToCart_addingValidProduct_emptyCart_shouldUpdateInCart() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    Cart cart = TestEntityFactory.createEmptyCart(customer);
    cart.setId(1L);
    CartItem cartItemResponse = TestEntityFactory.createCartItem(cart, product, 1);
    cartItemResponse.setId(1L);
    int addedProductQuantity = 2;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(cart));
    when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
        .thenReturn(Optional.of(cartItemResponse));

    cartService.updateProductInCart(new UpdateCartRequest(product.getId(), addedProductQuantity));

    // Then
    verify(cartItemRepository).save(cartItemCaptor.capture());

    CartItem savedCartItem = cartItemCaptor.getValue();

    assertEquals(cart.getId(), savedCartItem.getCart().getId());
    assertEquals(product.getId(), savedCartItem.getProduct().getId());
    assertEquals(addedProductQuantity, savedCartItem.getQuantity());

    verify(cartRepository).save(cartCaptor.capture());
    Cart savedCart = cartCaptor.getValue();

    assertEquals(cart.getId(), savedCart.getId());
    assertEquals(cart.getUser(), savedCart.getUser());
  }

  @Test
  void
      updateProductToCart_addingValidProductWithQuantityFour_TwoExistingProductInCart_shouldUpdateQuantityInFour() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    Cart cart = TestEntityFactory.createEmptyCart(customer);
    cart.setId(1L);
    int existingProductQuantity = 2;
    CartItem cartItemResponse =
        TestEntityFactory.createCartItem(cart, product, existingProductQuantity);
    cartItemResponse.setId(1L);
    int addedProductQuantity = 4;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(cart));
    when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
        .thenReturn(Optional.of(cartItemResponse));

    cartService.updateProductInCart(new UpdateCartRequest(product.getId(), addedProductQuantity));

    // Then
    verify(cartItemRepository).save(cartItemCaptor.capture());

    CartItem savedCartItem = cartItemCaptor.getValue();

    assertEquals(cart.getId(), savedCartItem.getCart().getId());
    assertEquals(product.getId(), savedCartItem.getProduct().getId());
    assertEquals(addedProductQuantity, savedCartItem.getQuantity());

    verify(cartRepository).save(cartCaptor.capture());
    Cart savedCart = cartCaptor.getValue();

    assertEquals(cart.getId(), savedCart.getId());
    assertEquals(cart.getUser(), savedCart.getUser());
  }

  @Test
  void
      updateProductToCart_addingValidProductWithQuantityTwo_FourExistingProductInCart_shouldUpdateQuantityInTwo() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    Cart cart = TestEntityFactory.createEmptyCart(customer);
    cart.setId(1L);
    int existingProductQuantity = 4;
    CartItem cartItemResponse =
        TestEntityFactory.createCartItem(cart, product, existingProductQuantity);
    cartItemResponse.setId(1L);
    int addedProductQuantity = 2;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(cart));
    when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
        .thenReturn(Optional.of(cartItemResponse));

    cartService.updateProductInCart(new UpdateCartRequest(product.getId(), addedProductQuantity));

    // Then
    verify(cartItemRepository).save(cartItemCaptor.capture());

    CartItem savedCartItem = cartItemCaptor.getValue();

    assertEquals(cart.getId(), savedCartItem.getCart().getId());
    assertEquals(product.getId(), savedCartItem.getProduct().getId());
    assertEquals(addedProductQuantity, savedCartItem.getQuantity());

    verify(cartRepository).save(cartCaptor.capture());
    Cart savedCart = cartCaptor.getValue();

    assertEquals(cart.getId(), savedCart.getId());
    assertEquals(cart.getUser(), savedCart.getUser());
  }

  @Test
  void updateProductToCart_productStockZero_shouldThrowInsufficientStockException() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    product.setStock(0);
    int addedProductQuantity = 2;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);

    // Then
    InsufficientStockException ex =
        assertThrows(
            InsufficientStockException.class,
            () ->
                cartService.updateProductInCart(
                    new UpdateCartRequest(product.getId(), addedProductQuantity)));

    assertEquals("Insufficient stock for product: " + product.getId(), ex.getMessage());
  }

  @Test
  void
      updateProductToCart_productStockLessThenUpdateQuantity_shouldThrowInsufficientStockException() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    product.setStock(1);
    int addedProductQuantity = 2;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);

    // Then
    InsufficientStockException ex =
        assertThrows(
            InsufficientStockException.class,
            () ->
                cartService.updateProductInCart(
                    new UpdateCartRequest(product.getId(), addedProductQuantity)));

    assertEquals("Insufficient stock for product: " + product.getId(), ex.getMessage());
  }

  @Test
  void
      updateProductToCart_addingValidProductWithQuantityZero_FourExistingProductInCart_shouldDeleteCartItem() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    Cart cart = TestEntityFactory.createEmptyCart(customer);
    cart.setId(1L);
    int existingProductQuantity = 4;
    CartItem cartItemResponse =
        TestEntityFactory.createCartItem(cart, product, existingProductQuantity);
    cartItemResponse.setId(1L);
    int addedProductQuantity = 0;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(cart));
    when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
        .thenReturn(Optional.of(cartItemResponse));

    cartService.updateProductInCart(new UpdateCartRequest(product.getId(), addedProductQuantity));

    // Then
    verify(cartItemRepository).delete(cartItemCaptor.capture());

    CartItem savedCartItem = cartItemCaptor.getValue();

    assertEquals(cart.getId(), savedCartItem.getCart().getId());
    assertEquals(product.getId(), savedCartItem.getProduct().getId());
    assertEquals(existingProductQuantity, savedCartItem.getQuantity());

    verify(cartRepository).save(cartCaptor.capture());
    Cart savedCart = cartCaptor.getValue();

    assertEquals(cart.getId(), savedCart.getId());
    assertEquals(cart.getUser(), savedCart.getUser());
  }

  @Test
  void
      updateProductInCart_deletingCartItemWithQuantityZero_cartDoesNotExist_shouldThrowCartNotFoundException() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    int updateProductQuantity = 0;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.empty());

    // Then
    CartNotFoundException ex =
        assertThrows(
            CartNotFoundException.class,
            () ->
                cartService.updateProductInCart(
                    new UpdateCartRequest(product.getId(), updateProductQuantity)));
    assertEquals("Cart not found for user", ex.getMessage());
  }

  @Test
  void
      updateProductInCart_deletingCartItemWithQuantityZero_cartItemDoesNotExist_shouldThrowCartItemNotFoundException() {
    // Given
    User customer = TestEntityFactory.createCustomer("customer");
    customer.setId(1L);
    User seller = TestEntityFactory.createSeller("seller");
    seller.setId(1L);
    Product product = TestEntityFactory.createProduct("Product", seller);
    product.setId(1L);
    Cart cart = TestEntityFactory.createEmptyCart(customer);
    cart.setId(1L);
    int updateProductQuantity = 0;
    // When
    when(authService.getCurrentUser()).thenReturn(customer);
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(cart));
    when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
        .thenReturn(Optional.empty());

    // Then
    CartItemNotFoundException ex =
        assertThrows(
            CartItemNotFoundException.class,
            () ->
                cartService.updateProductInCart(
                    new UpdateCartRequest(product.getId(), updateProductQuantity)));
    assertEquals("Item not found in Cart", ex.getMessage());
  }

  private void assertCartResponse(List<CartItemDto> cartItemDtos, CartResponse response) {
    assertNotNull(response, "CartResponse should not be null");

    // ---- cart-level assertions ----
    assertEquals(cartItemDtos.size(), response.items().size(), "Item count mismatch");

    int expectedTotalQuantity = cartItemDtos.stream().mapToInt(CartItemDto::quantity).sum();

    int expectedTotalPrice = cartItemDtos.stream().mapToInt(CartItemDto::subTotalPrice).sum();

    assertEquals(expectedTotalQuantity, response.totalQuantity(), "Total quantity mismatch");

    assertEquals(expectedTotalPrice, response.totalPrice(), "Total price mismatch");

    if (cartItemDtos.isEmpty()) {
      assertTrue(response.items().isEmpty(), "Items should be empty");
      assertNull(response.updatedAt(), "cartUpdatedAt should be null");
      return;
    }

    assertNotNull(response.updatedAt(), "cartUpdatedAt should not be null");

    // ---- map response items by cartItemId ----
    Map<Long, CartItemResponse> responseById =
        response.items().stream()
            .collect(Collectors.toMap(CartItemResponse::cartItemId, Function.identity()));

    assertEquals(cartItemDtos.size(), responseById.size(), "Items size mismatch");

    // ---- assert each DTO against its response ----
    for (CartItemDto dto : cartItemDtos) {
      CartItemResponse item = responseById.get(dto.cartItemId());
      assertNotNull(item, "Missing CartItemResponse for cartItemId=" + dto.cartItemId());

      assertEquals(dto.quantity(), item.quantity(), "quantity mismatch");
      assertEquals(dto.subTotalPrice(), item.subTotalPrice(), "subTotalPrice mismatch");

      // ---- product listing assertions ----
      ProductListing product = item.product();
      assertNotNull(product, "ProductListing should not be null");

      assertEquals(dto.productId(), product.id(), "productId mismatch");
      assertEquals(dto.productName(), product.name(), "productName mismatch");
      assertEquals(dto.sellerId(), product.sellerId(), "sellerId mismatch");
      assertEquals(dto.productStock(), product.stock(), "stock mismatch");
      assertEquals(dto.productPrice(), product.price(), "price mismatch");
    }
  }
}
