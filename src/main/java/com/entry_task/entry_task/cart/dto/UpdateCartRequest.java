package com.entry_task.entry_task.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCartRequest(
    @NotNull(message = "Product ID must not be null")
        @Positive(message = "Product ID must be a positive number")
        @Schema(description = "ID of the product to update", example = "123")
        Long productId,
    @PositiveOrZero(message = "Quantity must be zero or a positive number")
        @Schema(description = "New quantity for the product", example = "2")
        int quantity) {}
