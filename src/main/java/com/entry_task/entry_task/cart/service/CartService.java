package com.entry_task.entry_task.cart.service;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.cart.dto.CartItemDto;
import com.entry_task.entry_task.cart.dto.CartItemResponse;
import com.entry_task.entry_task.cart.dto.CartResponse;
import com.entry_task.entry_task.cart.dto.UpdateCartRequest;
import com.entry_task.entry_task.cart.entity.Cart;
import com.entry_task.entry_task.cart.entity.CartItem;
import com.entry_task.entry_task.cart.repository.CartItemRepository;
import com.entry_task.entry_task.cart.repository.CartRepository;
import com.entry_task.entry_task.exceptions.CartItemNotFoundException;
import com.entry_task.entry_task.exceptions.CartNotFoundException;
import com.entry_task.entry_task.exceptions.InsufficientStockException;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.service.ProductService;
import com.entry_task.entry_task.user.entity.User;
import jakarta.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.List;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductService productService;
  private final AuthService authService;

  public CartService(
      CartRepository cartRepository,
      CartItemRepository cartItemRepository,
      ProductService productService,
      AuthService authService) {
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productService = productService;
    this.authService = authService;
  }

  // TODO remove cart item if quantity is 0 or item inactive
  @PreAuthorize("hasRole('CUSTOMER')")
  public CartResponse getCart() {
    User currentUser = authService.getCurrentUser();
    List<CartItemDto> cartItems = cartItemRepository.findCartItemDtos(currentUser.getId());
    List<CartItemResponse> items =
        cartItems.stream()
            .map(
                dto ->
                    new CartItemResponse(
                        dto.cartItemId(),
                        dto.quantity(),
                        new ProductListing(
                            dto.productId(),
                            dto.productName(),
                            dto.sellerId(),
                            dto.productStock(),
                            dto.productPrice()),
                        dto.subTotalPrice()))
            .toList();
    return new CartResponse(
        items.size(),
        items.stream().mapToInt(CartItemResponse::quantity).sum(),
        items.stream().mapToInt(CartItemResponse::subTotalPrice).sum(),
        items,
        items.isEmpty() ? null : cartItems.get(0).cartUpdatedAt());
  }

  private String deleteProductFromCart(Long userId, Long productId) {
    Product product = productService.getActiveProductById(productId);
    Cart cart =
      cartRepository
        .findByUserId(userId)
        .orElseThrow(() -> new CartNotFoundException("Cart not found for user"));
    CartItem cartItem =
      cartItemRepository
        .findByCartIdAndProductId(cart.getId(), product.getId())
        .orElseThrow(() -> new CartItemNotFoundException("Item not found in Cart"));
    cartItemRepository.delete(cartItem);
    cart.setmTime(Instant.now().getEpochSecond());
    cartRepository.save(cart);
    return "deleted from cart";
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  @Transactional
  @Retryable(retryFor = {OptimisticLockException.class})
  public String updateProductInCart(UpdateCartRequest request) {
    User customer = authService.getCurrentUser();

    if (request.quantity() == 0) {
      return deleteProductFromCart(customer.getId(), request.productId());
    }

    Product product = productService.getActiveProductById(request.productId());
    if (product.getStock() <= 0 || request.quantity() > product.getStock()) {
      throw new InsufficientStockException("Insufficient stock for product: " + product.getId());
    }
    Cart cart =
        cartRepository
            .findByUserId(customer.getId())
            .orElseGet(
                () -> {
                  long now = Instant.now().getEpochSecond();
                  Cart newCart = new Cart(customer, now, now);
                  return cartRepository.save(newCart);
                });

    CartItem cartItem =
        cartItemRepository
            .findByCartIdAndProductId(cart.getId(), product.getId())
            .orElseGet(
                () -> {
                  long now = Instant.now().getEpochSecond();
                  return new CartItem(cart, product, 1, now, now);
                });

    cartItem.setQuantity(request.quantity());
    cartItem.setMTime(Instant.now().getEpochSecond());
    cart.setmTime(Instant.now().getEpochSecond());

    cartItemRepository.save(cartItem);
    cartRepository.save(cart);

    return "cart updated";
  }

  public List<CartItem> findAllCartItemsByIdAndUser(List<Long> itemIds, User user) {
    return cartItemRepository.findAllByIdAndUser(itemIds, user);
  }

  @Transactional
  public void deleteCartItems(List<CartItem> cartItems) {
    cartItemRepository.deleteAll(cartItems);
  }
}
