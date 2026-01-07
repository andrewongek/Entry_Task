package com.entry_task.entry_task.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Pagination information for paginated requests")
public record Pagination(
    @Min(value = 0, message = "Page index must be 0 or greater")
        @Schema(description = "Current page index (0-based)", example = "0")
        int page,
    @Min(value = 1, message = "Page size must be at least 1")
        @Schema(description = "Number of items per page", example = "10")
        int size) {}
