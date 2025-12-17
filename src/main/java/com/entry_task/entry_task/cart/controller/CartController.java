package com.entry_task.entry_task.cart.controller;


import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.cart.dto.CartResponse;
import com.entry_task.entry_task.cart.dto.UpdateCartRequest;
import com.entry_task.entry_task.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Endpoints for viewing and updating user's cart")
@Validated
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @Operation(summary = "Updates cart item quantity", description = "Handles adding, updating and deleting from the user's cart based on the productId, and quantity")
    @PostMapping()
    public ResponseEntity<CustomApiResponse<Void>> addToCartByProductId(@Valid @RequestBody UpdateCartRequest request) {
        String message = cartService.addProductToCart(request);
        return ResponseEntity.ok().body(CustomApiResponse.success(message, null));
    }

    @Operation(
            summary = "Get user's cart",
            description = "Retrieves the current contents of the user's shopping cart, including items, total quantity, and total price."
    )
    @GetMapping()
    public ResponseEntity<CustomApiResponse<CartResponse>> getCartItemList() {
        CartResponse response = cartService.getCart();
        return ResponseEntity.ok().body(CustomApiResponse.success("success", response));
    }
}
