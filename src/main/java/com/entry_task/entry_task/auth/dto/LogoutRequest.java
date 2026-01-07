package com.entry_task.entry_task.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request parameters for logging out and invalidating the refresh token")
public record LogoutRequest(
    @NotBlank
        @Schema(
            description = "Refresh token issued during authentication",
            example = "d75980fb-2aa0-42e8-a485-ca8c1da6c890")
        String refreshToken) {}
