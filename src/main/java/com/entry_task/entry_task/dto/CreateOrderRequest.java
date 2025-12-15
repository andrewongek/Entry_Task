package com.entry_task.entry_task.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(@NotEmpty List<Long> cartItemIds) {
}
