package com.entry_task.entry_task.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of an order")
public enum OrderStatus {

    @Schema(description = "Order has been created but not yet paid")
    CREATED,

    @Schema(description = "Order has been paid")
    PAID,

    @Schema(description = "Order has been shipped to the customer")
    SHIPPED,

    @Schema(description = "Order has been delivered and completed")
    COMPLETED,

    @Schema(description = "Order has been cancelled")
    CANCELLED
}