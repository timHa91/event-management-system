package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.model.EmailAddress;
import com.th.eventmanagmentsystem.usermanagement.domain.repository.UserRepository;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueEmailPolicyTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UniqueEmailPolicy uniqueEmailPolicy;

    @Test
    @DisplayName("Check sollte keine Exception werfen wenn Email unique ist")
    void whenEmailIsUnique_checkShouldPass() {
        //Arrange
        String validEmail = "test@test.com";
        var request = new UserRegistrationRequest(
                validEmail,
                "validPassword"
        );
        when(userRepository.existsByEmail(EmailAddress.of(validEmail))).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> uniqueEmailPolicy.check(request));
    }

    @DisplayName("Check solte eine Exception werfen wenn Email bereits existiert")
    @Test
    void whenEmailAlreadyExits_checkShouldThrowException() {
        // Arrange
        String duplicateEmail = "test@test.com";
        var request = new UserRegistrationRequest(
                duplicateEmail,
                "validPassword"
        );
        String expectedErrorMessage = "Email already exists";
        when(userRepository.existsByEmail(EmailAddress.of(duplicateEmail))).thenReturn(true);

        EmailAlreadyExistsException thrownException = assertThrows(EmailAlreadyExistsException.class, () -> {
            uniqueEmailPolicy.check(request);
        });

        assertEquals(expectedErrorMessage, thrownException.getMessage());
    }
}