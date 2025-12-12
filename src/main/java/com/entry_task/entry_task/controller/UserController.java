package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.ProductInfoDto;
import com.entry_task.entry_task.dto.ProductListResponse;
import com.entry_task.entry_task.dto.ProductListingDto;
import com.entry_task.entry_task.dto.ProductsListRequest;
import com.entry_task.entry_task.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final ProductService productService;

    public UserController(ProductService productService) {
        this.productService = productService;
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
}
