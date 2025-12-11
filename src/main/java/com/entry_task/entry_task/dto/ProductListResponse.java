package com.entry_task.entry_task.dto;

import java.util.List;

public record ProductListResponse(List<ProductInfoDto> products, MetadataDto metadata) {
}
