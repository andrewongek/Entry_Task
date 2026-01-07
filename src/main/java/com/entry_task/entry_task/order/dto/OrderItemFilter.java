package com.entry_task.entry_task.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record OrderItemFilter(
    @Schema(description = "keyword for search of product name") String keyword,
    @Schema(description = "Filter by userId of the buyer") Long userId,
    @Schema(description = "Filter by orderId of the Order") Long orderId,
    @Schema(description = "Get orderItem by the id") Long orderItemId,
    @Schema(
            description =
                "List of order statuses to filter the results by. "
                    + "Each value must be one of the OrderStatus enum values: "
                    + "CREATED, PAID, SHIPPED, COMPLETED, CANCELLED. "
                    + "If multiple statuses are provided, orders matching any of the statuses will be returned.",
            example = "[\"PAID\", \"SHIPPED\"]")
        List<String> statuses) {}
