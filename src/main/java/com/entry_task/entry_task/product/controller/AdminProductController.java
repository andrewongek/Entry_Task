package com.entry_task.entry_task.product.controller;

import com.entry_task.entry_task.common.api.ApiResponse;
import com.entry_task.entry_task.product.dto.*;
import com.entry_task.entry_task.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products/search")
    public ResponseEntity<ApiResponse<ProductListResponse<ProductInfo>>> getProductsInfoList(@RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getAdminProductInfoList(request, null);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @PostMapping("/{sellerId}/products/search")
    public ResponseEntity<ApiResponse<ProductListResponse<ProductInfo>>> getSellerProductsInfoList(@PathVariable Long sellerId, @RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getAdminProductInfoList(request, sellerId);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetails(@PathVariable Long productId) {
        ProductDetailResponse productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", productDetailDto));
    }

    @PostMapping("/{sellerId}/products")
    public ResponseEntity<ApiResponse<CreateProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request, @PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success("Product created", new CreateProductResponse(productService.createProductAdmin(request, sellerId))));
    }

    @PostMapping("/products/{productId}/activate")
    public ResponseEntity<ApiResponse<?>> activateProductById(@PathVariable Long productId) {
        productService.activateProduct(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    @PostMapping("/products/{productId}/deactivate")
    public ResponseEntity<ApiResponse<?>> deactivateProductById(@PathVariable Long productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }
}
