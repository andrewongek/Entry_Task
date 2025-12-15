package com.entry_task.entry_task.dto;

public record OrderItemDto(Long productId, String name, int quantity, int price, int subTotalPrice) {
}
