package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.common.dto.Pagination;
import com.entry_task.entry_task.common.dto.Sort;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

public record AdminOrderInvoiceItemListRequest(

        @Schema(description = "Optional SellerId filter")
        Long sellerId,

        @Schema(description = "Pagination settings for the order invoice item list")
        @Valid
        Pagination pagination,

        @Schema(description = "Filters to apply on the order invoice item list")
        OrderItemFilter filter,

        @Schema(description = "Sorting options for the order invoice item list")
        @Valid
        Sort sort
) {
    public AdminOrderInvoiceItemListRequest {
        if (pagination == null) {
            pagination = new Pagination(0, 10); // default page 0, size 10
        }
        if (sort == null) {
            sort = new Sort("id", "ASC"); // default sort by ID ascending
        }
    }
}

