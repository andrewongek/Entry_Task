package com.entry_task.entry_task.cart.dto;

import com.entry_task.entry_task.product.dto.ProductListing;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CartItemResponse", description = "Represents an item in the shopping cart")
public record CartItemResponse(
    @Schema(description = "Unique identifier for the cart item", example = "123") Long cartItemId,
    @Schema(description = "Quantity of the product in the cart", example = "2") int quantity,
    @Schema(description = "Product details for this cart item") ProductListing product,
    @Schema(description = "Subtotal price for this cart item (quantity * price)", example = "1998")
        int subTotalPrice) {}
