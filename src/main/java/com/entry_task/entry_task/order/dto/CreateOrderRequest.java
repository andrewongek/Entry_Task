package com.entry_task.entry_task.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Request to create a new order from cart items")
public record CreateOrderRequest(
    @NotEmpty(message = "Cart item IDs must not be empty")
        @Schema(
            description = "List of cart item IDs to include in the order",
            example = "[101, 102, 103]")
        List<Long> cartItemIds) {}
