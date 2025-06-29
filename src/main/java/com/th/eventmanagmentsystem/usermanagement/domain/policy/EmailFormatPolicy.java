package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.InvalidEmailFormatException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailFormatPolicy implements RegistrationPolicy<UserRegistrationRequest> {

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Override
    public void check(UserRegistrationRequest request) {
        if (request.email() == null || !Pattern.matches(EMAIL_PATTERN, request.email())) {
            throw new InvalidEmailFormatException();
        }
    }
}