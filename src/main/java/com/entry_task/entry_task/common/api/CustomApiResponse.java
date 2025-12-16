package com.entry_task.entry_task.common.api;

public record CustomApiResponse<T>(
        boolean success,
        String message,
        T data
) {
    public static <T> CustomApiResponse<T> success(String message, T data) {
        return new CustomApiResponse<>(true, message, data);
    }

    public static <T> CustomApiResponse<T> error(String message) {
        return new CustomApiResponse<>(false, message, null);
    }
}
