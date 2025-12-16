package com.entry_task.entry_task.cart.dto;

import java.util.List;

public record CartResponse(
        int totalItems,
        int totalQuantity,
        int totalPrice,
        List<CartItemResponse> items,
        Long updatedAt
) {
}
