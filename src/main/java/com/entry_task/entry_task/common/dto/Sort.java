package com.entry_task.entry_task.common.dto;

import static com.entry_task.entry_task.common.RegexPatterns.IDENTIFIER_PATTERN;
import static com.entry_task.entry_task.common.RegexPatterns.SORT_ORDER_PATTERN;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Sorting options for list endpoints")
public record Sort(
    @Schema(description = "Field to sort by", example = "id")
        @Pattern(regexp = IDENTIFIER_PATTERN, message = "Sort field contains invalid characters")
        String field,
    @Schema(description = "Sort order, either ASC or DESC", example = "ASC")
        @Pattern(
            regexp = SORT_ORDER_PATTERN,
            message = "Sort order must be either ASC or DESC, case insensitive")
        String order) {}
