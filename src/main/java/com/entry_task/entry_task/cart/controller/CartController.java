package com.entry_task.entry_task.cart.controller;


import com.entry_task.entry_task.common.api.ApiResponse;
import com.entry_task.entry_task.cart.dto.CartResponse;
import com.entry_task.entry_task.cart.dto.UpdateCartRequest;
import com.entry_task.entry_task.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add to Cart
    @PostMapping()
    public ResponseEntity<ApiResponse<?>> addToCartByProductId(@RequestBody UpdateCartRequest request) {
        String message = cartService.addProductToCart(request);
        return ResponseEntity.ok().body(ApiResponse.success(message, null));
    }

    // View Cart
    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getCartItemList() {
        CartResponse response = cartService.getCart();
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }
}
