package com.entry_task.entry_task.auth.service;

import com.entry_task.entry_task.auth.dto.LoginRequest;
import com.entry_task.entry_task.user.entity.User;

import java.util.Map;

public interface AuthService {
    User getCurrentUser();

    Map<String, Object> login(LoginRequest loginRequest);

    Map<String, String> refresh(String requestToken);

    void delete(String requestToken);
}
