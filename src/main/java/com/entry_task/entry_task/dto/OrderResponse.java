package com.entry_task.entry_task.dto;

public record OrderResponse(Long orderId, String status, int totalAmount, Long cTime, Long mTime) {
}