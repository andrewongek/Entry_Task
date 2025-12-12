package com.entry_task.entry_task.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required")
        String name,

        @Min(value = 0, message = "Price must be 0 or greater")
        int price,

        @Min(value = 0, message = "Stock must be 0 or greater")
        int stock,

        @NotEmpty(message = "At least one category is required")
        Set<Long> categoryIds,

        @NotBlank(message = "Description is required")
        String description
) {
}
