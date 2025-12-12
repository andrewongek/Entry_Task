package com.entry_task.entry_task.dto;

import java.util.List;

public record ProductListResponse<T>(List<T> products, MetadataDto metadata) {
}
