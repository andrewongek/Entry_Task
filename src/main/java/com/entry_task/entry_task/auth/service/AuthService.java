package com.entry_task.entry_task.auth.service;

import com.entry_task.entry_task.auth.dto.LoginRequest;
import com.entry_task.entry_task.auth.dto.TokenResponse;
import com.entry_task.entry_task.user.entity.User;

public interface AuthService {
  User getCurrentUser();

  TokenResponse login(LoginRequest loginRequest);

  TokenResponse refresh(String requestToken);

  void delete(String requestToken);
}
