package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Detailed product information returned by product detail APIs")
public record ProductDetailResponse(
    @Schema(description = "Unique identifier of the product", example = "1001") long id,
    @Schema(description = "Name of the product", example = "Pokemon Booster Pack") String name,
    @Schema(description = "Seller ID who owns this product", example = "42") long sellerId,
    @Schema(description = "Available stock quantity", example = "100") int stock,
    @Schema(description = "Price of the product in cents", example = "1299") int price,
    @Schema(description = "Category IDs associated with this product", example = "[1, 3, 5]")
        List<Long> categories,
    @Schema(
            description = "Detailed description of the product",
            example = "A sealed booster pack containing 10 random cards")
        String description,
    @Schema(description = "Current status of the product", example = "ACTIVE")
        ProductStatus productStatus,
    @Schema(description = "Creation timestamp (epoch milliseconds)", example = "1702790400000")
        long cTime,
    @Schema(description = "Last modified timestamp (epoch milliseconds)", example = "1702876800000")
        long mTime) {}
