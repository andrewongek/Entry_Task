package com.entry_task.entry_task.cart.dto;


public record CartItemDto(
        Long cartItemId,
        int quantity,
        Long productId,
        String productName,
        int productPrice,
        int productStock,
        Long sellerId,
        int subTotalPrice,
        Long cartUpdatedAt
) {
}