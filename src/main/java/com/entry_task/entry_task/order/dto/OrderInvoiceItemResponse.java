package com.entry_task.entry_task.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Information for an Item in an Order Invoice")
public record OrderInvoiceItemResponse(
        @Schema(description = "Unique id for the item in an Order")
        Long orderItemId,
        @Schema(description = "Unique id for the Order")
        Long orderId,
        @Schema(description = "Buyer's userId")
        Long userId,
        @Schema(description = "Purchased product's id")
        Long productId,
        @Schema(description = "Purchased product's name")
        String productName,
        @Schema(description = "No. of this product purchased in the Order")
        Integer quantity,
        @Schema(description = "Price of the product")
        Integer price,
        @Schema(description = "Total cost of the product(s) in the Order")
        Integer totalPrice,
        @Schema(description = "Status of the Order")
        String orderStatus,
        @Schema(description = "Time of the Order")
        Long orderCTime,
        @Schema(description = "Time of the last update to Order status")
        Long orderMTime
) {
}
