package com.entry_task.entry_task.product.dto;

import java.util.List;

public record ProductFilter(List<Integer> statuses, List<Long> categoryIds) {

}
