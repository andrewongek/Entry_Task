package com.entry_task.entry_task.services;

import com.entry_task.entry_task.model.RefreshToken;
import com.entry_task.entry_task.repository.RefreshTokenRepository;
import com.entry_task.entry_task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshTokenService(RefreshTokenRepository repo, UserService userService) {
        this.refreshTokenRepository = repo;
        this.userService = userService;
    }

    public RefreshToken createRefreshToken(Long userId) {
        var token = new RefreshToken();
        token.setUser(userService.getUserById(userId));
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(token);
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public Optional<RefreshToken> findByToken(String requestToken) {
        return refreshTokenRepository.findByToken(requestToken);
    }

    public void deleteToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
