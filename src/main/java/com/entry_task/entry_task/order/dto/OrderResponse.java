package com.entry_task.entry_task.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing an order")
public record OrderResponse(

        @Schema(description = "Unique identifier of the order", example = "1001")
        Long orderId,

        @Schema(description = "Current status of the order", example = "PENDING")
        String status,

        @Schema(description = "Total amount of the order in cents", example = "1500")
        int totalAmount,

        @Schema(description = "Creation timestamp of the order (milliseconds since epoch)", example = "1700000000000")
        Long cTime,

        @Schema(description = "Last modified timestamp of the order (milliseconds since epoch)", example = "1700000500000")
        Long mTime
) {}