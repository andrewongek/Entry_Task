package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.common.dto.Metadata;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of order invoice items")
public record OrderInvoiceItemListResponse(
    @Schema(description = "List of order invoice item information ")
        List<OrderInvoiceItemResponse> orderInvoiceItemList,
    @Schema(
            description =
                "Pagination and metadata information for the ordered product invoice list")
        Metadata metadata) {}
