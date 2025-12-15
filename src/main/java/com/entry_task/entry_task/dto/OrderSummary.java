package com.entry_task.entry_task.dto;

import com.entry_task.entry_task.enums.OrderStatus;
import com.entry_task.entry_task.model.OrderItem;

import java.util.List;

public record OrderSummary(Long orderId,
                           int totalItems,
                           int totalQuantity,
                           int totalPrice,
                           List<OrderItemDto> orderItems,
                           OrderStatus status,
                           Long cTime,
                           Long mTime
) {
}
