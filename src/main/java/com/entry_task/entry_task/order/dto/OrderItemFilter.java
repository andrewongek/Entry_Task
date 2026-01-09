package com.entry_task.entry_task.order.dto;

import static com.entry_task.entry_task.common.RegexPatterns.KEYWORD_PATTERN;

import com.entry_task.entry_task.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record OrderItemFilter(
    @Schema(description = "keyword for search of product name")
        @Pattern(regexp = KEYWORD_PATTERN, message = "Keyword contains invalid characters")
        String keyword,
    @Schema(description = "Filter by userId of the buyer") @Positive Long userId,
    @Schema(description = "Filter by orderId of the Order") @Positive Long orderId,
    @Schema(description = "Get orderItem by the id") @Positive Long orderItemId,
    @Schema(
            description =
                "List of order statuses to filter the results by. "
                    + "Each value must be one of the OrderStatus enum values: "
                    + "CREATED, PAID, SHIPPED, COMPLETED, CANCELLED. "
                    + "If multiple statuses are provided, orders matching any of the statuses will be returned.",
            example = "[\"PAID\", \"SHIPPED\"]")
        List<@NotNull OrderStatus> statuses) {}
