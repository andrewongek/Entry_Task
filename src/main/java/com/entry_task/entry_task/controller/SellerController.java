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
    public ResponseEntity<ApiResponse<?>> getProductsInfoList(@RequestBody ProductsListRequest request) {
        ProductListResponse<ProductInfoDto> responseData = productService.getSellerProductInfoList(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<?>> getProductDetailsById(@PathVariable Long id) {
        ProductDetailDto productDetailDto = productService.getSellerProductDetail(id);
        return ResponseEntity.ok().body(ApiResponse.success("success", productDetailDto));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<?>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product created", new CreateProductResponse(productService.createProduct(request))));
    }

    @PostMapping("/products/{id}/delete")
    public ResponseEntity<ApiResponse<?>> deleteProductById(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }
}
