package com.entry_task.entry_task.product.dto;

import static com.entry_task.entry_task.common.RegexPatterns.KEYWORD_PATTERN;

import jakarta.validation.constraints.*;
import java.util.Set;

public record UpdateProductRequest(
    @NotBlank(message = "Product name is required")
        @Pattern(regexp = KEYWORD_PATTERN, message = "Product name contains invalid characters")
        String name,
    @Positive(message = "Price must be positive") int price,
    @PositiveOrZero(message = "Stock must be 0 or greater") int stock,
    @NotEmpty(message = "At least one category is required") Set<@Positive Long> categoryIds,
    @NotBlank(message = "Description is required")
        @Size(min = 10, max = 2_000, message = "Description must be between 10 and 2000 characters")
        String description) {}
