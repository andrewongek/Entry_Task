package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.ToCartRequest;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.model.Cart;
import com.entry_task.entry_task.model.CartItem;
import com.entry_task.entry_task.model.Product;
import com.entry_task.entry_task.model.User;
import com.entry_task.entry_task.repository.CartItemRepository;
import com.entry_task.entry_task.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final AuthService authService;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductService productService,
                       AuthService authService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.authService = authService;
    }

    @Transactional
    public void addProductToCart(ToCartRequest request) {
        User user = authService.getCurrentUser();
        if (user.getRole() != Role.USER) {
            throw new AccessDeniedException("Only normal users can favourite products");
        }

        if (request.quantity() == 0) {
            deleteProductFromCart(user.getId(), request.productId());
            return;
        }

        Product product = productService.getActiveProductById(request.productId());
        if (product.getStock() <= 0) {
            throw new IllegalStateException("Product is out of stock");
        }

        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            long now = Instant.now().getEpochSecond();
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setcTime(now);
            newCart.setmTime(now);
            return newCart;
        });

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElseGet(() -> {
            long now = Instant.now().getEpochSecond();
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setcTime(now);
            newCartItem.setmTime(now);
            return newCartItem;
        });

        cartItem.setQuantity(request.quantity());
        cartItemRepository.save(cartItem);
    }

    private void deleteProductFromCart(Long userId, Long productId) {
        Product product = productService.getActiveProductById(productId);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new IllegalStateException("Cart not found for user"));
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElseThrow(() -> new IllegalStateException("Product not found in Cart"));
        cartItemRepository.delete(cartItem);
    }
}
