package com.entry_task.entry_task.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pagination metadata for list responses")
public record Metadata(

        @Schema(description = "Total number of items available", example = "100")
        long totalItems,

        @Schema(description = "Current page number (0-based index)", example = "0")
        int currentPage,

        @Schema(description = "Number of items per page", example = "20")
        int pageSize,

        @Schema(description = "Indicates if there is a next page", example = "true")
        boolean hasNext
) {
}