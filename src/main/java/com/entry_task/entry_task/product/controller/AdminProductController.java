package com.entry_task.entry_task.product.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
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
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductInfo>>> getProductsInfoList(@RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getAdminProductInfoList(request, null);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @PostMapping("/{sellerId}/products/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductInfo>>> getSellerProductsInfoList(@PathVariable Long sellerId, @RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getAdminProductInfoList(request, sellerId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<ProductDetailResponse>> getProductDetails(@PathVariable Long productId) {
        ProductDetailResponse productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", productDetailDto));
    }

    @PostMapping("/{sellerId}/products")
    public ResponseEntity<CustomApiResponse<CreateProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request, @PathVariable Long sellerId) {
        return ResponseEntity.ok(CustomApiResponse.success("Product created", new CreateProductResponse(productService.createProductAdmin(request, sellerId))));
    }

    @PostMapping("/products/{productId}/activate")
    public ResponseEntity<CustomApiResponse<?>> activateProductById(@PathVariable Long productId) {
        productService.activateProduct(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }

    @PostMapping("/products/{productId}/deactivate")
    public ResponseEntity<CustomApiResponse<?>> deactivateProductById(@PathVariable Long productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }
}
