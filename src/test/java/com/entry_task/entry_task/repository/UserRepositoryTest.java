package com.entry_task.entry_task.repository;

import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_shouldReturnUser() {
        User user = new User("john", "john@mail.com", "pass", Role.USER);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("john");

        assertTrue(result.isPresent());
    }

    @Test
    void findByUsername_shouldReturnEmpty() {
        assertTrue(userRepository.findByUsername("nope").isEmpty());
    }
}