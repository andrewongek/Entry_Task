package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.*;
import com.entry_task.entry_task.model.UserFavourite;
import com.entry_task.entry_task.services.CartService;
import com.entry_task.entry_task.services.ProductService;
import com.entry_task.entry_task.services.UserFavouriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final ProductService productService;
    private final UserFavouriteService userFavouriteService;
    private final CartService cartService;

    public UserController(ProductService productService, UserFavouriteService userFavouriteService, CartService cartService) {
        this.productService = productService;
        this.userFavouriteService = userFavouriteService;
        this.cartService = cartService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<?>> getProductListingList(@RequestBody ProductsListRequest request) {
        ProductListResponse<ProductListingDto> responseData = productService.getUserProductListingList(request, null);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/{sellerId}/products")
    public ResponseEntity<ApiResponse<?>> getSellerProductListingList(@PathVariable Long sellerId, @RequestBody ProductsListRequest request) {
        ProductListResponse<ProductListingDto> responseData = productService.getUserProductListingList(request, sellerId);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<?>> getProductInfo(@PathVariable Long productId) {
        ProductInfoDto productInfoDto = productService.getProductInfo(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", productInfoDto));
    }

    @GetMapping("/favourites")
    public ResponseEntity<ApiResponse<?>> getUserFavouriteProductListingList(@RequestBody ProductsListRequest request) {
        ProductListResponse<ProductListingDto> responseData = productService.getUserFavouriteProductListingList(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @PostMapping("/products/{productId}/favourite")
    public ResponseEntity<ApiResponse<?>> setUserFavouriteByProductId(@PathVariable Long productId) {
        userFavouriteService.setUserFavouriteByProductId(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));

    }

    @DeleteMapping("/products/{productId}/favourite")
    public ResponseEntity<ApiResponse<?>> deleteUserFavouriteByProductId(@PathVariable Long productId) {
        userFavouriteService.deleteUserFavouriteByProductId(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    // Add to Cart
     @PostMapping("/products")
    public ResponseEntity<ApiResponse<?>> addToCartByProductId(@RequestBody ToCartRequest request) {
        String message = cartService.addProductToCart(request);
        return ResponseEntity.ok().body(ApiResponse.success(message, null));
    }

    // View Cart
     @GetMapping("/cart")
    public ResponseEntity<ApiResponse<?>> getCartItemList() {
        CartListResponse response = cartService.getCart();
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }

}
