package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.WeakPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StrongPasswordPolicyTest {

    private final StrongPasswordPolicy strongPasswordPolicy = new StrongPasswordPolicy();


    @DisplayName("Sollte keine Exception werfen wenn Password stark ist")
    @ParameterizedTest(name = "Prüfe gültiges Password: {0}")
    @ValueSource(strings = {
            "StrongP@ss1",
            "Secure123!",
            "C0mpl3x@P4ssw0rd",
            "P@$w0rd123"
    })
    void whenPasswordIsStrong_checkShouldPass(String validPassword) {
        var request = new UserRegistrationRequest(
                "test@test.com",
                validPassword
        );

        assertDoesNotThrow(() -> strongPasswordPolicy.check(request));
    }

    @DisplayName("Sollte Exception werfen wenn Password zu schwach")
    @ParameterizedTest(name = "Prüfe ungültiges Password: {0}")
    @ValueSource(strings = {
            "123",
            "password",
            "12345678",
            "qwerty",
            "abcdef",
            "ALLCAPS",
            "233T.",
            "nodigits",
            "NoSpecial1",
            "no-uppercase-1",
            "NO-LOWERCASE-1"
    })
    void whenPasswordToWeak_checkShouldNotPass(String weakPassword) {
        var request = new UserRegistrationRequest(
                "test@test.com",
                weakPassword
        );

        assertThrows(WeakPasswordException.class, () -> {
            strongPasswordPolicy.check(request);
        });
    }
}