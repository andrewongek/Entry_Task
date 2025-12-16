package com.entry_task.entry_task.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "ProductListing",
    description = "A summary view of a product for listing purposes"
)
public record ProductListing(
    @Schema(
        description = "Unique identifier for the product",
        example = "101"
    )
    Long id,

    @Schema(
        description = "Name of the product",
        example = "Wireless Mouse"
    )
    String name,

    @Schema(
        description = "Unique identifier of the seller of this product",
        example = "42"
    )
    long sellerId,

    @Schema(
        description = "Number of items available in stock",
        example = "150"
    )
    int stock,

    @Schema(
        description = "Price of the product in cents (e.g., 2999 for $29.99)",
        example = "2999"
    )
    int price
) {
}
