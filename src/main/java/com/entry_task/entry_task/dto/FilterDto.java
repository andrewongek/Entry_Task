package com.entry_task.entry_task.dto;

import java.util.List;

public record FilterDto(List<Integer> statuses, List<Long> categoryIds) {

}
