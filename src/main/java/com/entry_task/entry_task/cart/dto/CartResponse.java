package com.entry_task.entry_task.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response object representing a user's shopping cart")
public record CartResponse(

        @Schema(description = "Total number of distinct items in the cart", example = "3")
        int totalItems,

        @Schema(description = "Total quantity of all items in the cart", example = "7")
        int totalQuantity,

        @Schema(description = "Total price of all items in the cart (in cents or smallest currency unit)", example = "3500")
        int totalPrice,

        @Schema(description = "List of cart items")
        List<CartItemResponse> items,

        @Schema(description = "Timestamp when the cart was last updated (milliseconds since epoch)", example = "1700000000000")
        Long updatedAt
) {}
