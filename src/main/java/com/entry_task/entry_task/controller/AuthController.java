package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.LoginDto;
import com.entry_task.entry_task.dto.RegisterDto;
import com.entry_task.entry_task.exceptions.InvalidRefreshTokenException;
import com.entry_task.entry_task.model.RefreshToken;
import com.entry_task.entry_task.security.JwtUtil;
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
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody RegisterDto registerDto) {

        userService.registerUser(registerDto.username(), registerDto.password(), registerDto.email(), registerDto.role());

        String message = registerDto.role() + " registered";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails.getUsername());
        long userId = userService.getIdByUsername(userDetails.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);
        var tokenData = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );
        return ResponseEntity.ok(ApiResponse.success("access", tokenData));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        return refreshTokenService.findByToken(requestToken).map(token -> {
            if (refreshTokenService.isTokenExpired(token)) {
                refreshTokenService.deleteToken(token);
                throw new InvalidRefreshTokenException("Refresh token expired. Please login again.");
            }
            String newJwt = jwtUtil.generateToken(token.getUser().getUsername());
            return ResponseEntity.ok(Map.of("token", newJwt));
        }).orElseThrow(()-> new InvalidRefreshTokenException("Invalid refresh token."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");

        if (requestToken == null || requestToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh token is required.");
        }

        return refreshTokenService.findByToken(requestToken).map(token -> {
            refreshTokenService.deleteToken(token);
            return ResponseEntity.ok("Logged out successfully.");
        }).orElseThrow(()-> new InvalidRefreshTokenException("Invalid refresh token."));
    }
}
