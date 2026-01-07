package com.entry_task.entry_task.auth.dto;

import com.entry_task.entry_task.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request parameters for user registration")
public record RegisterRequest(
    @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        @Schema(description = "Desired username. Must be unique", example = "testuser1")
        String username,
    @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "Password for the account", example = "P@ssw0rd")
        String password,
    @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "User email address", example = "testuser@example.com")
        String email,
    @NotNull(message = "Role is required")
        @Schema(description = "Role of the user", example = "CUSTOMER")
        Role role) {}
