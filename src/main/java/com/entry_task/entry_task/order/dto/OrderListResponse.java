package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.common.dto.Metadata;

import java.util.List;

public record OrderListResponse(List<OrderSummary> orderSummaries, Metadata metadata) {
}
