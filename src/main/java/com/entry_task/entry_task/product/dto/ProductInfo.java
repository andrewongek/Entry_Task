package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product information returned by the API")
public record ProductInfo(
    @Schema(description = "Unique product identifier", example = "1") Long id,
    @Schema(description = "Product name", example = "iPhone 15") String name,
    @Schema(description = "Seller ID who owns the product", example = "100") Long sellerId,
    @Schema(description = "Available stock quantity", example = "50") int stock,
    @Schema(description = "Product price in smallest currency unit (e.g. cents)", example = "99900")
        int price,
    @Schema(
            description = "Detailed product description",
            example = "Latest Apple iPhone with A17 chip")
        String description,
    @Schema(description = "Current product status", example = "ACTIVE") ProductStatus status) {}
