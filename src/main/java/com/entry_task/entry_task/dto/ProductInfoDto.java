package com.entry_task.entry_task.dto;

import com.entry_task.entry_task.enums.ProductStatus;

public record ProductInfoDto(Long id, String name, long sellerId, int stock, int price, String description, ProductStatus status){
}
