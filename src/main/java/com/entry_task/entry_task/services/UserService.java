package com.entry_task.entry_task.services;

import com.entry_task.entry_task.enums.Role;
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

    public void registerUser(String username, String password, String email, Role role) {
        userValidator.validateNewUser(username, email, password, role);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        newUser.setRole(role);

        userRepository.save(newUser);
    }

    public Optional<User> getUserExistsBy(long id) {
        return userRepository.findById(id);
    }

    public long getIdByUsername(String username) {
         var user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
         return user.getId();
    }
}
