package com.entry_task.entry_task.auth.service;

import com.entry_task.entry_task.auth.dto.LoginRequest;
import com.entry_task.entry_task.auth.dto.TokenResponse;
import com.entry_task.entry_task.auth.entity.RefreshToken;
import com.entry_task.entry_task.exceptions.InvalidRefreshTokenException;
import com.entry_task.entry_task.security.JwtUtil;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.repository.UserRepository;
import com.entry_task.entry_task.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final UserRepository userRepository;
  private final UserService userService;
  private final RefreshTokenService refreshTokenService;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public AuthServiceImpl(
      UserRepository userRepository,
      UserService userService,
      RefreshTokenService refreshTokenService,
      AuthenticationManager authenticationManager,
      JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.refreshTokenService = refreshTokenService;
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal().equals("anonymousUser")) {
      throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
    }

    String username = authentication.getName();

    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Override
  public TokenResponse login(LoginRequest loginRequest) {
    String username = loginRequest.username().trim();
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, loginRequest.password()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String accessToken = jwtUtil.generateToken(userDetails.getUsername());
    User user = userService.getUserByUsername(userDetails.getUsername());
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
    log.info("User logged in successfully: userId={}", user.getId());
    return new TokenResponse(accessToken, refreshToken.getToken());
  }

  @Override
  @Transactional
  public TokenResponse refresh(String requestToken) {
    RefreshToken token =
        refreshTokenService
            .findByToken(requestToken)
            .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token."));

    if (refreshTokenService.isTokenExpired(token)) {
      refreshTokenService.deleteToken(token);
      throw new InvalidRefreshTokenException("Refresh token expired. Please login again.");
    }

    String newAccessToken = jwtUtil.generateToken(token.getUser().getUsername());
    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(token.getUser());
    refreshTokenService.deleteToken(token);

    return new TokenResponse(newAccessToken, newRefreshToken.getToken());
  }

  @Override
  @Transactional
  public void delete(String requestToken) {
    if (requestToken == null || requestToken.isBlank()) {
      throw new InvalidRefreshTokenException("Refresh token is required.");
    }

    var token =
        refreshTokenService
            .findByToken(requestToken)
            .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token."));

    refreshTokenService.deleteToken(token);
  }
}
