package com.entry_task.entry_task.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request parameters for logging out and invalidating the refresh token")
public record LogoutRequest(
        @NotBlank
        @Schema(description = "Refresh token issued during authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {
}
