package com.entry_task.entry_task.common.dto;

public record Metadata(long totalItems, int currentPage, int pageSize, boolean hasNext) {
}
