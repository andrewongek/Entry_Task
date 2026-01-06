package com.entry_task.entry_task.auth.service;

import com.entry_task.entry_task.auth.entity.RefreshToken;
import com.entry_task.entry_task.auth.repository.RefreshTokenRepository;
import com.entry_task.entry_task.user.entity.User;
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

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.refreshTokenRepository = repo;
    }

    public RefreshToken createRefreshToken(User user) {
        var token = new RefreshToken(
                user,
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(refreshTokenDurationMs)
        );
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
