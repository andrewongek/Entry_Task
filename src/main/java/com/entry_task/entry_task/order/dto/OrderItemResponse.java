package com.entry_task.entry_task.order.dto;

public record OrderItemResponse(Long productId, String name, int quantity, int price, int subTotalPrice) {
}
