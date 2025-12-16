package com.entry_task.entry_task.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartRequest(
        @NotNull(message = "Product ID must not be null")
        @Schema(description = "ID of the product to update", example = "123")
        Long productId,

        @Min(value = 0, message = "Quantity cannot be negative")
        @Schema(description = "New quantity for the product", example = "2")
        int quantity
) {
}