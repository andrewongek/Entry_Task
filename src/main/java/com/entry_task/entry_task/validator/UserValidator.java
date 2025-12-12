package com.entry_task.entry_task.validator;

import com.entry_task.entry_task.dto.RegisterDto;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateNewAdmin(RegisterDto registerDto) {
        if (!Role.ADMIN.equals(registerDto.role())) {
            throw new IllegalArgumentException("Role must be ADMIN");
        }
        validateNewCommon(registerDto);
    }

    public void validateNewUser(RegisterDto registerDto) {
        if (!Role.USER.equals(registerDto.role()) && !Role.SELLER.equals(registerDto.role())) {
            throw new IllegalArgumentException("Role must be either USER or SELLER");
        }
        validateNewCommon(registerDto);
    }

    public void validateNewCommon(RegisterDto registerDto) {
        if (registerDto.username() == null || registerDto.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (registerDto.password() == null || registerDto.password().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        if (registerDto.email() == null || registerDto.email().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (userRepository.findByUsername(registerDto.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
    }
}
