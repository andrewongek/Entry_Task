package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.LoginDto;
import com.entry_task.entry_task.dto.RegisterDto;
import com.entry_task.entry_task.exceptions.InvalidRefreshTokenException;
import com.entry_task.entry_task.model.RefreshToken;
import com.entry_task.entry_task.security.JwtUtil;
import com.entry_task.entry_task.services.AuthService;
import com.entry_task.entry_task.services.RefreshTokenService;
import com.entry_task.entry_task.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        String message = registerDto.role() + " registered";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PostMapping("/admin/register")
    public ResponseEntity<ApiResponse<?>> registerAdmin(@RequestBody RegisterDto registerDto) {
        userService.registerAdmin(registerDto);
        String message = registerDto.role() + " registered";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody LoginDto loginDto) {
        Map<String, Object> tokenData = authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("access", tokenData));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        Map<String, String> newJwt = authService.refresh(requestToken);
        return ResponseEntity.ok(ApiResponse.success("refresh", newJwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        authService.delete(requestToken);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully.", null));
    }
}
