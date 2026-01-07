package com.entry_task.entry_task.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Schema(description = "Filter criteria for products")
public record ProductFilter(
    @Schema(
            description =
                "List of product statuses to filter by. " + "0 = DELETED, 1 = ACTIVE, 2 = INACTIVE",
            example = "[1, 2]")
        List<@Min(0) Integer> statuses,
    @Schema(description = "List of category IDs to filter products", example = "[1, 2, 3]")
        List<@Positive Long> categoryIds) {}
