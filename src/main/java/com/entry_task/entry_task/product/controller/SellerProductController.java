package com.entry_task.entry_task.product.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
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
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductInfo>>> getSellerProductInfoList(@RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getSellerProductInfoList(request);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<ProductDetailResponse>> getSellerProductDetail(@PathVariable Long productId) {
        ProductDetailResponse productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", productDetailDto));
    }

    @PostMapping("/products")
    public ResponseEntity<CustomApiResponse<CreateProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(CustomApiResponse.success("Product created", new CreateProductResponse(productService.createProduct(request))));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<Void>> updateProductById(@Valid @RequestBody UpdateProductRequest request, @PathVariable Long productId) {
        productService.updateProductById(productId, request);
        return ResponseEntity.ok().body(CustomApiResponse.success("Product updated", null));
    }
}
