package com.entry_task.entry_task.user.validator;

import com.entry_task.entry_task.auth.dto.RegisterRequest;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateNewAdmin(RegisterRequest registerRequest) {

        validateNewCommon(registerRequest);
    }

    public void validateNewUser(RegisterRequest registerRequest) {
        if (!Role.USER.equals(registerRequest.role()) && !Role.SELLER.equals(registerRequest.role())) {
            throw new IllegalArgumentException("Role must be either USER or SELLER");
        }
        validateNewCommon(registerRequest);
    }

    public void validateNewCommon(RegisterRequest registerRequest) {
        if (registerRequest.username() == null || registerRequest.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (registerRequest.password() == null || registerRequest.password().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        if (registerRequest.email() == null || registerRequest.email().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
    }
}
