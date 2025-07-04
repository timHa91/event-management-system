package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.WeakPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class StrongPasswordPolicy implements RegistrationPolicy<UserRegistrationRequest> {

    // This pattern requires:
    // - At least 8 characters
    // - At least one uppercase letter
    // - At least one lowercase letter
    // - At least one digit
    // - At least one special character
    private static final String STRONG_PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_])(?=\\S+$).{8,}$";

    @Override
    public void check(UserRegistrationRequest request) {
        if (request.password() == null || !Pattern.matches(STRONG_PASSWORD_PATTERN, request.password())) {
            throw new WeakPasswordException();
        }
    }
}
