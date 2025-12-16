package com.entry_task.entry_task.product.controller;

import com.entry_task.entry_task.common.api.ApiResponse;
import com.entry_task.entry_task.product.dto.*;
import com.entry_task.entry_task.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@PreAuthorize("hasRole('SELLER')")
@RequestMapping("/api/seller")
public class SellerProductController {
    private final ProductService productService;

    public SellerProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products/search")
    public ResponseEntity<ApiResponse<ProductListResponse<ProductInfo>>> getSellerProductInfoList(@RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getSellerProductInfoList(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getSellerProductDetail(@PathVariable Long productId) {
        ProductDetailResponse productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", productDetailDto));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<CreateProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product created", new CreateProductResponse(productService.createProduct(request))));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProductById(@Valid @RequestBody UpdateProductRequest request, @PathVariable Long productId) {
        productService.updateProductById(productId, request);
        return ResponseEntity.ok().body(ApiResponse.success("Product updated", null));
    }
}
