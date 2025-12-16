package com.entry_task.entry_task.user.service;

import com.entry_task.entry_task.auth.dto.RegisterRequest;
import com.entry_task.entry_task.exceptions.UserNotFoundException;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.repository.UserRepository;
import com.entry_task.entry_task.user.validator.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, UserValidator userValidator) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public void registerUser(RegisterRequest registerRequest) {
        userValidator.validateNewUser(registerRequest);
        register(registerRequest);

    }

    public void registerAdmin(RegisterRequest registerRequest) {
        userValidator.validateNewAdmin(registerRequest);
        register(registerRequest);
    }

    private void register(RegisterRequest registerRequest) {
        User newUser = new User(registerRequest.username(), passwordEncoder.encode(registerRequest.password()), registerRequest.email(), registerRequest.role());
        userRepository.save(newUser);
    }

    public long getIdByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return user.getId();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public User findUserBySellerId(Long sellerId) {
        return getUserById(sellerId);
    }

    public void validateSellerId(Long sellerId) {
        getUserById(sellerId);
    }
}
