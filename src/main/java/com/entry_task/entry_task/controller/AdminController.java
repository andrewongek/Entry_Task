package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.*;
import com.entry_task.entry_task.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<?>> getProductsInfoList(@RequestBody ProductsListRequest request) {
        ProductListResponse<ProductInfoDto> responseData = productService.getAdminProductInfoList(request, null);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/{sellerId}/products")
    public ResponseEntity<ApiResponse<?>> getSellerProductsInfoList(@PathVariable Long sellerId, @RequestBody ProductsListRequest request) {
        ProductListResponse<ProductInfoDto> responseData = productService.getAdminProductInfoList(request, sellerId);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<?>> getProductDetails(@PathVariable Long productId) {
        ProductDetailDto productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", productDetailDto));
    }

    @PostMapping("/{sellerId}/products")
    public ResponseEntity<ApiResponse<?>> createProduct(@Valid @RequestBody CreateProductRequest request, @PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success("Product created", new CreateProductResponse(productService.createProductAdmin(request, sellerId))));
    }

    @PostMapping("/products/{id}/activate")
    public ResponseEntity<ApiResponse<?>> activateProductById(@PathVariable Long id) {
        productService.activateProduct(id);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    @PostMapping("/products/{id}/deactivate")
    public ResponseEntity<ApiResponse<?>> deactivateProductById(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    @PostMapping("/products/{id}/delete")
    public ResponseEntity<ApiResponse<?>> deleteProductById(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }
}
