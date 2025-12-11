package com.entry_task.entry_task.dto;

import com.entry_task.entry_task.enums.Role;

public record RegisterDto(String username, String password, String email, Role role){
}
