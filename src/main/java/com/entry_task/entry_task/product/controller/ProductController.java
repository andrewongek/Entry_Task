package com.entry_task.entry_task.product.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.product.dto.ProductInfo;
import com.entry_task.entry_task.product.dto.ProductListRequest;
import com.entry_task.entry_task.product.dto.ProductListResponse;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductListing>>> getProductListingList(@RequestBody ProductListRequest request) {
        ProductListResponse<ProductListing> responseData = productService.getUserProductListingList(request, null);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @PostMapping("/{sellerId}/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductListing>>> getSellerProductListingList(@PathVariable Long sellerId, @RequestBody ProductListRequest request) {
        ProductListResponse<ProductListing> responseData = productService.getUserProductListingList(request, sellerId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CustomApiResponse<ProductInfo>> getProductInfo(@PathVariable Long productId) {
        ProductInfo productInfo = productService.getProductInfo(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", productInfo));
    }
}
