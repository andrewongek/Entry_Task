package com.entry_task.entry_task.auth.dto;

import static com.entry_task.entry_task.common.RegexPatterns.UUID_PATTERN;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request parameters for refreshing authentication token")
public record RefreshTokenRequest(
    @NotBlank
        @Schema(
            description = "Refresh token issued during authentication",
            example = "d75980fb-2aa0-42e8-a485-ca8c1da6c890")
        @Pattern(regexp = UUID_PATTERN, message = "Invalid refresh token format")
        String refreshToken) {}
