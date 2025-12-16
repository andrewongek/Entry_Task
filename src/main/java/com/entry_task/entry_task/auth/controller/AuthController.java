package com.entry_task.entry_task.auth.controller;

import com.entry_task.entry_task.common.api.ApiResponse;
import com.entry_task.entry_task.auth.dto.LoginRequest;
import com.entry_task.entry_task.auth.dto.RegisterRequest;
import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        String message = registerRequest.role() + " registered";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }


    @PostMapping("/admin/register")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        userService.registerAdmin(registerRequest);
        String message = registerRequest.role() + " registered";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> tokenData = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("access", tokenData));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        Map<String, String> newJwt = authService.refresh(requestToken);
        return ResponseEntity.ok(ApiResponse.success("refresh", newJwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        authService.delete(requestToken);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully.", null));
    }
}
