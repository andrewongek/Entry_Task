package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.enums.ProductStatus;

public record ProductInfo(Long id, String name, long sellerId, int stock, int price, String description,
                          ProductStatus status) {
}
