package com.entry_task.entry_task.product.dto;

import com.entry_task.entry_task.enums.ProductStatus;

import java.util.List;

public record ProductDetailResponse(long id, String name, long sellerId, int stock, int price, List<Long> categories,
                                    String description, ProductStatus productStatus, long cTime, long mTime) {
}
