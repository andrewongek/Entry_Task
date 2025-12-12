package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.*;
import com.entry_task.entry_task.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/seller")
public class SellerController {
    private final ProductService productService;

    public SellerController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<?>> getSellerProductInfoList(@RequestBody ProductsListRequest request) {
        ProductListResponse<ProductInfoDto> responseData = productService.getSellerProductInfoList(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<?>> getSellerProductDetail(@PathVariable Long productId) {
        ProductDetailDto productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", productDetailDto));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<?>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product created", new CreateProductResponse(productService.createProduct(request))));
    }

    @PostMapping("/products/{productId}/delete")
    public ResponseEntity<ApiResponse<?>> deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    @PostMapping("/products/{productId}/update")
    public ResponseEntity<ApiResponse<?>> updateProductById(@Valid @RequestBody ProductRequest request, @PathVariable Long productId) {
        productService.updateProductById(productId, request);
        return ResponseEntity.ok().body(ApiResponse.success("Product updated", null));
    }
}
