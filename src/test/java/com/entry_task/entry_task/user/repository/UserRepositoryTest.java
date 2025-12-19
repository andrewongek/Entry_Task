package com.entry_task.entry_task.user.repository;

import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_shouldReturnUser() {
        User user = new User("john", "john@mail.com", "pass", Role.CUSTOMER);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("john");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findByUsername_caseSensitive_shouldNotReturnUser() {
        User user = new User("john", "john@mail.com", "pass", Role.CUSTOMER);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("John");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUsername_userNotRegistered_shouldReturnEmpty() {
        assertTrue(userRepository.findByUsername("mary").isEmpty());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User user = new User("john", "john@mail.com", "pass", Role.CUSTOMER);
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("john@mail.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findByEmail_userNotRegistered_shouldReturnEmpty() {
        assertTrue(userRepository.findByUsername("mary@mail.com").isEmpty());
    }

    @Test
    void findByEmail_caseSensitive_shouldNotReturnUser() {
        User user = new User("john", "john@mail.com", "pass", Role.CUSTOMER);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("John@mail.com");

        assertTrue(result.isEmpty());
    }

}