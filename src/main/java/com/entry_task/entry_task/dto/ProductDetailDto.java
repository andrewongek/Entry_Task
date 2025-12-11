package com.entry_task.entry_task.dto;

import com.entry_task.entry_task.enums.ProductStatus;

import java.util.List;

public record ProductDetailDto(long id, String name, long sellerId, int stock, int price, List<Long> categories, String description, ProductStatus productStatus, long cTime, long mTime){
}
