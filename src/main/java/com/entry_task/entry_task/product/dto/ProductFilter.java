package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Schema(description = "Filter criteria for products")
public record ProductFilter(
    @Schema(
            description =
                "List of product statuses to filter by. " + "0 = DELETED, 1 = ACTIVE, 2 = INACTIVE",
            example = "[1, 2]")
        List<@NotNull ProductStatus> statuses,
    @Schema(description = "List of category IDs to filter products", example = "[1, 2, 3]")
        List<@Positive Long> categoryIds) {}
