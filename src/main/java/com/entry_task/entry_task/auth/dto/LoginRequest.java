package com.entry_task.entry_task.auth.dto;

import static com.entry_task.entry_task.common.RegexPatterns.ALPHANUMERIC_PATTERN;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request parameters for user login")
public record LoginRequest(
    @NotBlank(message = "Username is required")
        @Pattern(
            regexp = ALPHANUMERIC_PATTERN,
            message = "Username must be alphanumeric without spaces")
        @Schema(description = "Registered user's chosen username", example = "testuser1")
        String username,
    @NotBlank(message = "Password is required")
        @Schema(description = "Password for the account", example = "P@ssw0rd")
        String password) {}
