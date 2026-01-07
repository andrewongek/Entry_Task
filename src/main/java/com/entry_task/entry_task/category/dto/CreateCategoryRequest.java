package com.entry_task.entry_task.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request parameters for creating new product category")
public record CreateCategoryRequest(
    @NotBlank(message = "Category name is required")
        @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
        @Schema(description = "New unique name for a category of products", example = "Pokémon")
        String name) {}
