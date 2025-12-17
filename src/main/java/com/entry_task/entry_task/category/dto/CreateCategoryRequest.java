package com.entry_task.entry_task.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request parameters for creating new product category")
public record CreateCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Schema(description = "New unique name for a category of products", example = "Pokémon")
        String name) {
}
