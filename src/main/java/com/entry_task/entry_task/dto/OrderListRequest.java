package com.entry_task.entry_task.dto;

public record OrderListRequest(
        String keyword,
        PaginationDto pagination,
        OrderFilterDto filter,
        SortDto sort
) {
    public OrderListRequest {
        if (pagination == null) {
            pagination = new PaginationDto(0, 10);
        }
        if (sort == null) {
            sort = new SortDto("id", "ASC");
        }
    }
}
