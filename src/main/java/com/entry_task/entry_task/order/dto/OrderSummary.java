package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Summary information for an order, including items, totals, and status")
public record OrderSummary(
    @Schema(description = "Unique identifier for the order", example = "1001") Long orderId,
    @Schema(description = "Total number of distinct items in the order", example = "2")
        int totalItems,
    @Schema(description = "Total quantity of all items in the order", example = "5")
        int totalQuantity,
    @Schema(description = "Total price of the order in cents", example = "2999") int totalPrice,
    @Schema(description = "List of order items") List<OrderItemResponse> orderItems,
    @Schema(description = "Current status of the order", example = "CREATED") OrderStatus status,
    @Schema(
            description = "Order creation timestamp in milliseconds since epoch",
            example = "1716710400000")
        Long cTime,
    @Schema(
            description = "Order last modified timestamp in milliseconds since epoch",
            example = "1716796800000")
        Long mTime) {}
