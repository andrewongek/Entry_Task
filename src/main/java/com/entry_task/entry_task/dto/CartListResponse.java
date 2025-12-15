package com.entry_task.entry_task.dto;

import java.util.List;

public record CartListResponse(
        int totalItems,
        int totalQuantity,
        int totalPrice,
        List<CartItemDto> items,
        Long updatedAt
) {}
