package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.common.dto.Pagination;
import com.entry_task.entry_task.common.dto.Sort;

public record ProductListRequest(
        String keyword,
        Pagination pagination,
        ProductFilter filter,
        Sort sort
) {
    public ProductListRequest {
        if (pagination == null) {
            pagination = new Pagination(0, 10);
        }
        if (sort == null) {
            sort = new Sort("id", "ASC");
        }
    }
}
