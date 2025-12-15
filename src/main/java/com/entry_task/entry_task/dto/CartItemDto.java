package com.entry_task.entry_task.dto;

public record CartItemDto(
        Long cartItemId,
        int quantity,
        ProductListingDto product,
        int subTotalPrice
        ) {}
