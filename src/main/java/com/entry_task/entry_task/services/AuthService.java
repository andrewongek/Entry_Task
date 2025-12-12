package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.LoginDto;
import com.entry_task.entry_task.model.User;

import java.util.Map;

public interface AuthService {
    User getCurrentUser();

    Map<String, Object> login(LoginDto loginDto);

    Map<String, String> refresh(String requestToken);

    void delete(String requestToken);
}
