package com.entry_task.entry_task.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sorting options for list endpoints")
public record Sort(
    @Schema(description = "Field to sort by", example = "id") String field,
    @Schema(description = "Sort order, either ASC or DESC", example = "ASC") String order) {}
