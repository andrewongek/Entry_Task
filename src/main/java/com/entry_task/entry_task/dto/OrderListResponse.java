package com.entry_task.entry_task.dto;

import java.util.List;

public record OrderListResponse(List<OrderSummary> orderSummaries, MetadataDto metadata) {
}
