package com.entry_task.entry_task.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Pagination information for paginated requests")
public record Pagination(
    @PositiveOrZero(message = "Page index must be zero or positive")
        @Schema(description = "Current page index (0-based)", example = "0")
        int page,
    @Positive(message = "Page size must be positive")
        @Schema(description = "Number of items per page", example = "10")
        int size) {}
