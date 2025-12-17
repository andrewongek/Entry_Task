package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.common.dto.Metadata;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response object containing a list of products and associated metadata for pagination")
public record ProductListResponse<T>(
        @Schema(description = "List of product items")
        List<T> products,
        @Schema(description = "Pagination and other metadata related to the product list")
        Metadata metadata
) {
}
