package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.common.dto.Metadata;

import java.util.List;

public record ProductListResponse<T>(List<T> products, Metadata metadata) {
}
