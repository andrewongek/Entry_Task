package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.ProductInfoDto;
import com.entry_task.entry_task.dto.ProductListResponse;
import com.entry_task.entry_task.dto.ProductListingDto;
import com.entry_task.entry_task.dto.ProductsListRequest;
import com.entry_task.entry_task.model.UserFavourite;
import com.entry_task.entry_task.services.ProductService;
import com.entry_task.entry_task.services.UserFavouriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final ProductService productService;
    private final UserFavouriteService userFavouriteService;

    public UserController(ProductService productService, UserFavouriteService userFavouriteService) {
        this.productService = productService;
        this.userFavouriteService = userFavouriteService;
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
    // @PostMapping("/products/{productId}")


    // Remove from Cart
    // @DelMapping("/products/{productId}")


    // View Cart
    // @GEtMapping("/cart")


}
