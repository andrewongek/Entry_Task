package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.common.dto.Pagination;
import com.entry_task.entry_task.common.dto.Sort;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(
    description =
        "Request parameters to fetch a paginated list of orders with optional filters and sorting")
public record OrderListRequest(
    @Schema(description = "Pagination settings for the order list") @Valid Pagination pagination,
    @Schema(description = "Filters to apply on the order list") @Valid OrderFilter filter,
    @Schema(description = "Sorting options for the order list") @Valid Sort sort) {
  public OrderListRequest {
    if (pagination == null) {
      pagination = new Pagination(0, 10); // default page 0, size 10
    }
    if (sort == null) {
      sort = new Sort("id", "ASC"); // default sort by ID ascending
    }
  }
}
