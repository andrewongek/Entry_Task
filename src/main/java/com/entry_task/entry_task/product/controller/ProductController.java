package com.entry_task.entry_task.product.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.product.dto.ProductInfo;
import com.entry_task.entry_task.product.dto.ProductListRequest;
import com.entry_task.entry_task.product.dto.ProductListResponse;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Product", description = "User operations related to products")
@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get product listing", description = "Retrieve a paginated list of products. Allows for sorting, filtering and searches")
    @PostMapping("/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductListing>>> getProductListingList(@Valid @RequestBody ProductListRequest request) {
        ProductListResponse<ProductListing> responseData = productService.getUserProductListingList(request, null);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @Operation(summary = "Get seller's product listing", description = "Retrieve a paginated list of a seller's products. Allows for sorting, filtering and searches")
    @PostMapping("/{sellerId}/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductListing>>> getSellerProductListingList(@PathVariable Long sellerId, @Valid @RequestBody ProductListRequest request) {
        ProductListResponse<ProductListing> responseData = productService.getUserProductListingList(request, sellerId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @Operation(summary = "Get a product's details", description = "Retrieve a particular product's details")
    @GetMapping("/{productId}")
    public ResponseEntity<CustomApiResponse<ProductInfo>> getProductInfo(@PathVariable Long productId) {
        ProductInfo productInfo = productService.getProductInfo(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", productInfo));
    }
}
