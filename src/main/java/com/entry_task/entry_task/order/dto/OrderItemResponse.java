package com.entry_task.entry_task.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response representing a single item in an order")
public record OrderItemResponse(
    @Schema(description = "ID of the product", example = "101") Long productId,
    @Schema(description = "Name of the product", example = "Laptop Model X") String name,
    @Schema(description = "Quantity of this product in the order", example = "2") int quantity,
    @Schema(
            description =
                "Price of a single unit of the product (in cents or smallest currency unit)",
            example = "500")
        int price,
    @Schema(description = "Subtotal price for this item (quantity * unit price)", example = "1000")
        int subTotalPrice) {}
