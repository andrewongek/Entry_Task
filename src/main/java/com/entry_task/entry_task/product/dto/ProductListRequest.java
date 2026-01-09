package com.entry_task.entry_task.product.dto;

import static com.entry_task.entry_task.common.RegexPatterns.KEYWORD_PATTERN;

import com.entry_task.entry_task.common.dto.Pagination;
import com.entry_task.entry_task.common.dto.Sort;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Schema(
    description =
        "Request parameters for fetching a list of products with optional filters, pagination, and sorting. Can be left empty for default")
public record ProductListRequest(
    @Schema(description = "Keyword to search products by name", example = "laptop")
        @Pattern(regexp = KEYWORD_PATTERN, message = "Category name contains invalid characters")
        String keyword,
    @Schema(description = "Pagination settings") @Valid Pagination pagination,
    @Schema(description = "Filters to apply on the product list") @Valid ProductFilter filter,
    @Schema(description = "Sorting options for the product list") @Valid Sort sort) {
  public ProductListRequest {
    if (pagination == null) {
      pagination = new Pagination(0, 10); // default page 0, size 10
    }
    if (sort == null) {
      sort = new Sort("id", "ASC"); // default sort by ID ascending
    }
  }
}
