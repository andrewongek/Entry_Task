package com.entry_task.entry_task.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeleteCategoryRequest(
        @NotBlank(message = "Category id is required")
        @Schema(description = "Id for the category that is to be deleted", example = "1")
        Long id
) {
}
