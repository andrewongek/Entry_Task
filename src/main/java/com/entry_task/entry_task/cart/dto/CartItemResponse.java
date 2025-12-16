package com.entry_task.entry_task.cart.dto;

import com.entry_task.entry_task.product.dto.ProductListing;

public record CartItemResponse(
        Long cartItemId,
        int quantity,
        ProductListing product,
        int subTotalPrice
) {
}
