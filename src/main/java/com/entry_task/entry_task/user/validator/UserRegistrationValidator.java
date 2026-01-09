package com.entry_task.entry_task.user.validator;

import com.entry_task.entry_task.auth.dto.RegisterRequest;
import com.entry_task.entry_task.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationValidator {

  private final UserRepository userRepository;

  public UserRegistrationValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void validateRegistrationRequest(RegisterRequest registerRequest) {
    if (userRepository.findByUsername(registerRequest.username().trim()).isPresent()) {
      throw new IllegalArgumentException("Username is already taken");
    }

    if (userRepository.findByEmail(registerRequest.email().trim().toLowerCase()).isPresent()) {
      throw new IllegalArgumentException("Email is already registered");
    }
  }
}
