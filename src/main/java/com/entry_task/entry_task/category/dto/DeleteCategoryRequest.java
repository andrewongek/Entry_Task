package com.entry_task.entry_task.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record DeleteCategoryRequest(
    @NotNull(message = "Category id is required")
        @Schema(description = "Id for the category that is to be deleted", example = "1")
        Long id) {}
