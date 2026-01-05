package com.entry_task.entry_task.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Response containing newly issued access and refresh tokens")
public record TokenResponse(
        @NotBlank
        @Schema(description = "Newly issued access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @NotBlank
        @Schema(description = "Newly issued refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {
}
