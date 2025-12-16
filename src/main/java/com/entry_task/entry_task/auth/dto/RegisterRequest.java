package com.entry_task.entry_task.auth.dto;

import com.entry_task.entry_task.enums.Role;

public record RegisterRequest(String username, String password, String email, Role role) {
}
