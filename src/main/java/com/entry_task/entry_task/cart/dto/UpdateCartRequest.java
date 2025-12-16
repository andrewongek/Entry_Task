package com.entry_task.entry_task.cart.dto;

public record UpdateCartRequest(
        Long productId,
        int quantity
) {
    public UpdateCartRequest {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID must not be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }
}