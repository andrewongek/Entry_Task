package com.entry_task.entry_task.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public record CustomApiResponse<T>(
    @Schema(description = "Indicates whether the request was successful", example = "true")
        boolean success,
    @Schema(
            description = "Human-readable message describing the result",
            example = "Product created successfully")
        String message,
    @Schema(description = "Actual response payload") T data) {
  public static <T> CustomApiResponse<T> success(String message, T data) {
    return new CustomApiResponse<>(true, message, data);
  }

  public static <T> CustomApiResponse<T> error(String message) {
    return new CustomApiResponse<>(false, message, null);
  }
}
