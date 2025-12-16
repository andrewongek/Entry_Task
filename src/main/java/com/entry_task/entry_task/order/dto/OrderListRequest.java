package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.common.dto.Pagination;
import com.entry_task.entry_task.common.dto.Sort;

public record OrderListRequest(
        String keyword,
        Pagination pagination,
        OrderFilter filter,
        Sort sort
) {
    public OrderListRequest {
        if (pagination == null) {
            pagination = new Pagination(0, 10);
        }
        if (sort == null) {
            sort = new Sort("id", "ASC");
        }
    }
}
