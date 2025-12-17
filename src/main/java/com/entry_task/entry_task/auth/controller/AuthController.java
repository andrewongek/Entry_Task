package com.entry_task.entry_task.auth.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.auth.dto.LoginRequest;
import com.entry_task.entry_task.auth.dto.RegisterRequest;
import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Auth", description = "Endpoints for user authentication and registration")
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with the specified role")
    @PostMapping("/register")
    public ResponseEntity<CustomApiResponse<Void>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        String message = registerRequest.role() + " registered";
        return ResponseEntity.ok(CustomApiResponse.success(message, null));
    }

    @Operation(summary = "Register a new admin user", description = "Creates a new admin user account with the specified role")
    @PostMapping("/admin/register")
    public ResponseEntity<CustomApiResponse<Void>> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerAdmin(registerRequest);
        String message = registerRequest.role() + " registered";
        return ResponseEntity.ok(CustomApiResponse.success(message, null));
    }

    @Operation(summary = "User login", description = "Authenticate user and return access token")
    @PostMapping("/login")
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> loginUser(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> tokenData = authService.login(loginRequest);
        return ResponseEntity.ok(CustomApiResponse.success("access", tokenData));
    }

    @Operation(summary = "Refresh JWT token", description = "Refresh access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<CustomApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        Map<String, String> newJwt = authService.refresh(requestToken);
        return ResponseEntity.ok(CustomApiResponse.success("refresh", newJwt));
    }

    @Operation(summary = "Logout user", description = "Invalidate refresh token and logout user")
    @PostMapping("/logout")
    public ResponseEntity<CustomApiResponse<Void>> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        authService.delete(requestToken);
        return ResponseEntity.ok(CustomApiResponse.success("Logged out successfully.", null));
    }
}
