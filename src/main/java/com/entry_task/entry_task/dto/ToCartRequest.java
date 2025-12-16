package com.entry_task.entry_task.dto;

public record ToCartRequest(
        Long productId,
        int quantity
) {
    public ToCartRequest {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID must not be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }
}