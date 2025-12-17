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

@Tag(name = "Seller Product", description = "Seller operations related to products")
@RestController
@Validated
@PreAuthorize("hasRole('SELLER')")
@RequestMapping("/api/seller")
public class SellerProductController {
    private final ProductService productService;

    public SellerProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get product listing", description = "Retrieve a paginated list of products. Allows for sorting, filtering and searches")
    @PostMapping("/products/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductInfo>>> getSellerProductInfoList(@Valid @RequestBody ProductListRequest request) {
        ProductListResponse<ProductInfo> responseData = productService.getSellerProductInfoList(request);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @Operation(summary = "Get a product's details", description = "Retrieve a particular product's details")
    @GetMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<ProductDetailResponse>> getSellerProductDetail(@PathVariable Long productId) {
        ProductDetailResponse productDetailDto = productService.getSellerProductDetail(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", productDetailDto));
    }

    @Operation(summary = "Create a product", description = "Creates a product. The product will belong to the seller.")
    @PostMapping("/products")
    public ResponseEntity<CustomApiResponse<CreateProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(CustomApiResponse.success("Product created", new CreateProductResponse(productService.createProduct(request))));
    }

    @Operation(summary = "Deletes a product", description = "Sets the product status to DELETED.")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }

    @Operation(summary = "Updates a product", description = "Creates a product. Updates the name, price, stock, categories and description.")
    @PutMapping("/products/{productId}")
    public ResponseEntity<CustomApiResponse<Void>> updateProductById(@Valid @RequestBody UpdateProductRequest request, @PathVariable Long productId) {
        productService.updateProductById(productId, request);
        return ResponseEntity.ok().body(CustomApiResponse.success("Product updated", null));
    }
}
