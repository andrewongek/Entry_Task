package com.entry_task.entry_task.user.service;

import com.entry_task.entry_task.auth.dto.RegisterRequest;
import com.entry_task.entry_task.auth.service.AuthServiceImpl;
import com.entry_task.entry_task.exceptions.UserNotFoundException;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.repository.UserRepository;
import com.entry_task.entry_task.user.validator.UserRegistrationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRegistrationValidator userRegistrationValidator;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, UserRegistrationValidator userRegistrationValidator) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userRegistrationValidator = userRegistrationValidator;
    }

    @PreAuthorize("hasAnyRole('SELLER', 'USER')")
    public void registerUser(RegisterRequest registerRequest) {
        userRegistrationValidator.validateRegistrationRequest(registerRequest);
        register(registerRequest);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void registerAdmin(RegisterRequest registerRequest) {
        userRegistrationValidator.validateRegistrationRequest(registerRequest);
        register(registerRequest);
    }

    private void register(RegisterRequest registerRequest) {
        User newUser = new User(registerRequest.username(), registerRequest.email(), passwordEncoder.encode(registerRequest.password()), registerRequest.role());
        newUser = userRepository.save(newUser);
        log.info("{} username: {} id: {} registered", registerRequest.role(), newUser.getUsername(), newUser.getId());
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
