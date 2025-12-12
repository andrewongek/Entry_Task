package com.entry_task.entry_task.dto;

public record ProductsListRequest(
        String keyword,
        PaginationDto pagination,
        FilterDto filter,
        SortDto sort
) {
    public ProductsListRequest {
        if (pagination == null) {
            pagination = new PaginationDto(0, 10);
        }
        if (sort == null) {
            sort = new SortDto("id", "ASC");
        }
    }
}
