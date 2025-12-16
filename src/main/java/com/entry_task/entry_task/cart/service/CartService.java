package com.entry_task.entry_task.cart.service;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.cart.dto.CartItemResponse;
import com.entry_task.entry_task.cart.dto.CartResponse;
import com.entry_task.entry_task.cart.dto.UpdateCartRequest;
import com.entry_task.entry_task.exceptions.CartItemNotFoundException;
import com.entry_task.entry_task.exceptions.CartNotFoundException;
import com.entry_task.entry_task.exceptions.InsufficientStockException;
import com.entry_task.entry_task.cart.entity.Cart;
import com.entry_task.entry_task.cart.entity.CartItem;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.cart.repository.CartItemRepository;
import com.entry_task.entry_task.cart.repository.CartRepository;
import com.entry_task.entry_task.cart.repository.projections.CartItemProjection;
import com.entry_task.entry_task.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final AuthService authService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductService productService, AuthService authService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.authService = authService;
    }

    @PreAuthorize("hasRole('USER')")
    public CartResponse getCart() {
        User currentUser = authService.getCurrentUser();
        List<CartItemProjection> projections = cartItemRepository.findCartItemProjections(currentUser.getId());
        List<CartItemResponse> items = projections.stream()
                .map(p -> new CartItemResponse(
                        p.getCartItemId(),
                        p.getQuantity(),
                        new ProductListing(
                                p.getProductId(),
                                p.getProductName(),
                                p.getSellerId(),
                                p.getProductStock(),
                                p.getProductPrice()),
                        p.getSubTotalPrice()))
                .toList();
        return new CartResponse(
                items.size(),
                items.stream()
                        .mapToInt(CartItemResponse::quantity)
                        .sum(),
                items.stream()
                        .mapToInt(CartItemResponse::subTotalPrice)
                        .sum(),
                items,
                items.isEmpty() ? null : projections.get(0).getCartUpdatedAt());
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public String addProductToCart(UpdateCartRequest request) {
        User user = authService.getCurrentUser();

        if (request.quantity() == 0) {
            return deleteProductFromCart(user.getId(), request.productId());
        }

        Product product = productService.getActiveProductById(request.productId());
        if (product.getStock() <= 0 || request.quantity() > product.getStock()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getId());
        }
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            long now = Instant.now().getEpochSecond();
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setcTime(now);
            newCart.setmTime(now);
            return cartRepository.save(newCart);
        });

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElseGet(() -> {
            long now = Instant.now().getEpochSecond();
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setCTime(now);
            newCartItem.setMTime(now);
            return newCartItem;
        });

        cartItem.setQuantity(request.quantity());
        cartItem.setMTime(Instant.now().getEpochSecond());

        cart.setmTime(Instant.now().getEpochSecond());
        cartRepository.save(cart);

        cartItemRepository.save(cartItem);
        return "cart updated";
    }

    @Transactional
    private String deleteProductFromCart(Long userId, Long productId) {
        Product product = productService.getActiveProductById(productId);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found for user"));
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElseThrow(() -> new CartItemNotFoundException("Item not found in Cart"));
        cartItemRepository.delete(cartItem);
        cart.setmTime(Instant.now().getEpochSecond());
        cartRepository.save(cart);
        return "deleted from cart";
    }

    public List<CartItem> findAllCartItemsByIdAndUser(List<Long> itemIds, User user) {
        return cartItemRepository.findAllByIdAndUser(itemIds, user);
    }

    public void deleteCartItems(List<CartItem> cartItems) {
        cartItemRepository.deleteAll(cartItems);
    }
}
