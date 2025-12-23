package com.entry_task.entry_task.product.dto.cache;

import com.entry_task.entry_task.enums.ProductStatus;

import java.util.Set;

public record ProductStatic(
        long id,
        String name,
        long sellerId,
        Set<Long> categoryIds,
        String description,
        ProductStatus status

) {
}
