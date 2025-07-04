package com.th.eventmanagmentsystem.usermanagement.domain.repository;

import com.th.eventmanagmentsystem.usermanagement.domain.model.EmailAddress;
import com.th.eventmanagmentsystem.usermanagement.domain.model.User;
import com.th.eventmanagmentsystem.usermanagement.domain.model.UserPassword;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenSavedAndFindByEmail_thenUserIsPersistedAndFound() {
        // Arrange
        EmailAddress validEmail = EmailAddress.of("test@test.com");
        User user = new User(
                validEmail,
                UserPassword.of("$2a$10$N9qo8uLOickgx2ZMRZoMye.IKbeT_IuTL0Fp2aEMavFXrLbpIRH/O")
        );
        userRepository.save(user);

        // Act
        User found =
                userRepository.findByEmail(validEmail).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(validEmail.email(),found.getEmail().email());
    }

    @Test
    void whenEmailIsAlreadySaved_thenUserAlreadyExists() {
        // Arrange
        EmailAddress validEmail = EmailAddress.of("test@test.com");

        User user = new User(
                validEmail,
                UserPassword.of("$2a$10$N9qo8uLOickgx2ZMRZoMye.IKbeT_IuTL0Fp2aEMavFXrLbpIRH/O")
        );
        userRepository.save(user);

        // Act
        boolean doesExist = userRepository.existsByEmail(validEmail);

        assertTrue(doesExist);
    }
}