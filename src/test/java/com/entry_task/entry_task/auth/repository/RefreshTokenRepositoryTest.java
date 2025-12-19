package com.entry_task.entry_task.auth.repository;

import com.entry_task.entry_task.auth.entity.RefreshToken;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.repository.UserRepository;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByToken_existInDb_shouldReturn() {
        User user = TestEntityFactory.createCustomer("customer");
        userRepository.save(user);
        RefreshToken token = TestEntityFactory.createRefreshToken(user);
        refreshTokenRepository.save(token);

        Optional<RefreshToken> result = refreshTokenRepository.findByToken(token.getToken());
        assertTrue(result.isPresent());
        assertEquals(token.getToken(), result.get().getToken());
    }

    @Test
    void findByToken_dontExistInDb_shouldReturn() {
        String token = UUID.randomUUID().toString();
        Optional<RefreshToken> result = refreshTokenRepository.findByToken(token);
        assertTrue(result.isEmpty());
    }
}