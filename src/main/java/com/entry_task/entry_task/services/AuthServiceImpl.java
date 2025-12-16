package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.LoginDto;
import com.entry_task.entry_task.exceptions.InvalidRefreshTokenException;
import com.entry_task.entry_task.model.RefreshToken;
import com.entry_task.entry_task.model.User;
import com.entry_task.entry_task.repository.UserRepository;
import com.entry_task.entry_task.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, UserService userService, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public Map<String, Object> login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails.getUsername());
        long userId = userService.getIdByUsername(userDetails.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );
    }

    @Override
    public Map<String, String> refresh(String requestToken) {
        return refreshTokenService.findByToken(requestToken).map(token -> {
            if (refreshTokenService.isTokenExpired(token)) {
                refreshTokenService.deleteToken(token);
                throw new InvalidRefreshTokenException("Refresh token expired. Please login again.");
            }
            String newJwt = jwtUtil.generateToken(token.getUser().getUsername());
            return Map.of("token", newJwt);
        }).orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token."));
    }

    @Override
    @Transactional
    public void delete(String requestToken) {
        if (requestToken == null || requestToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh token is required.");
        }

        var token = refreshTokenService.findByToken(requestToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token."));

        refreshTokenService.deleteToken(token);
    }
}
