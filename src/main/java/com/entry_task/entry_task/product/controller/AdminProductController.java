package com.entry_task.entry_task.product.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.product.dto.*;
import com.entry_task.entry_task.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Product", description = "Admin operations related to products")
@Validated
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get product listing", description = "Retrieve a paginated list of products. Allows for sorting, filtering and searches")
    @PostMapping("/products/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductInfo>>> getProductsInfoList(@Valid @RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getAdminProductInfoList(request, null);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @Operation(summary = "Get seller's product listing", description = "Retrieve a paginated list of a seller's products. Allows for sorting, filtering and searches")
    @PostMapping("/{sellerId}/products/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductInfo>>> getSellerProductsInfoList(@PathVariable Long sellerId, @Valid @RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getAdminProductInfoList(request, sellerId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @Operation(summary = "Get a product's details", description = "Retrieve a particular product's details")
    @GetMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<ProductDetailResponse>> getProductDetails(@PathVariable Long productId) {
        ProductDetailResponse productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", productDetailDto));
    }

    @Operation(summary = "Create a product for seller", description = "Creates a product. The product will belong to the sellerId provided.")
    @PostMapping("/{sellerId}/products")
    public ResponseEntity<CustomApiResponse<CreateProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request, @PathVariable Long sellerId) {
        return ResponseEntity.ok(CustomApiResponse.success("Product created", new CreateProductResponse(productService.createProductAdmin(request, sellerId))));
    }

    @Operation(summary = "Set product status to ACTIVE", description = "Sets a product that is status inactive to active")
    @PostMapping("/products/{productId}/activate")
    public ResponseEntity<CustomApiResponse<?>> activateProductById(@PathVariable Long productId) {
        productService.activateProduct(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }

    @Operation(summary = "Set product status to INACTIVE", description = "Sets a product that is status active to inactive")
    @PostMapping("/products/{productId}/deactivate")
    public ResponseEntity<CustomApiResponse<?>> deactivateProductById(@PathVariable Long productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }
}
