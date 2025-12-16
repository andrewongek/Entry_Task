package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.common.dto.Metadata;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of user orders")
public record OrderListResponse(

        @Schema(description = "List of summarized orders")
        List<OrderSummary> orderSummaries,

        @Schema(description = "Pagination and metadata information for the order list")
        Metadata metadata
) {}