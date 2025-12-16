package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.RegisterDto;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.exceptions.UserNotFoundException;
import com.entry_task.entry_task.model.User;
import com.entry_task.entry_task.repository.UserRepository;
import com.entry_task.entry_task.validator.UserValidator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public void registerUser(RegisterDto registerDto) {
        userValidator.validateNewUser(registerDto);
        register(registerDto);

    }

    public void registerAdmin(RegisterDto registerDto) {
        userValidator.validateNewAdmin(registerDto);
        register(registerDto);
    }

    private void register(RegisterDto registerDto) {
        User newUser = new User(registerDto.username(), passwordEncoder.encode(registerDto.password()), registerDto.email(), registerDto.role());
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
