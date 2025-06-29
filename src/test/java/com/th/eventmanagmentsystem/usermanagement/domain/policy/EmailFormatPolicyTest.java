package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.InvalidEmailFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailFormatPolicyTest {

    private final EmailFormatPolicy emailFormatPolicy = new EmailFormatPolicy();

    @DisplayName("Sollte bei gültigen E-Mail-Formaten keine Exception werfen")
    @ParameterizedTest(name = "Prüfe gültige E-Mail: {0}")
    @ValueSource(strings = {
            "test@example.com",
            "test.user@example.co.uk",
            "user_name+alias@gmail.com",
            "user-name@sub.domain.org"
    })
    void whenEmailIsValid_checkShouldPass(String validEmail) {
        var request = new UserRegistrationRequest(
                validEmail,
                "validPassword"
        );
        // Assert: Wir erwarten, dass dieser Aufruf erfolgreich ist und keine Exception fliegt.
        assertDoesNotThrow(() -> {
            emailFormatPolicy.check(request);
        });
    }

    @DisplayName("Sollte bei ungültigen E-Mail-Formaten eine Exception werfen")
    @ParameterizedTest(name = "Prüfe ungültige Eingabe: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "test@example",
            "test.example.com",
            "@example.com",
            "test@.com"
    })
    void whenEmailIsInvalid_checkShouldThrowException(String invalidEmail) {
        var request = new UserRegistrationRequest(
                invalidEmail,
                "validPassword"
        );

        // Assert: Wir erwarten, dass bei diesen Eingaben IMMER eine InvalidEmailFormatException fliegt.
        assertThrows(InvalidEmailFormatException.class, () -> {
            emailFormatPolicy.check(request);
        });
    }

}