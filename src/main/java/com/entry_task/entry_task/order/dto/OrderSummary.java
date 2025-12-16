package com.entry_task.entry_task.order.dto;

import com.entry_task.entry_task.enums.OrderStatus;

import java.util.List;

public record OrderSummary(
        Long orderId,
        int totalItems,
        int totalQuantity,
        int totalPrice,
        List<OrderItemResponse> orderItems,
        OrderStatus status,
        Long cTime,
        Long mTime
) {
}
