package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.WeakPasswordException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StrongPasswordPolicy implements RegistrationPolicy<UserRegistrationRequest> {

    private Validator validator;

    @Override
    public void check(UserRegistrationRequest request) {
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        for (var violation : violations) {
            if (violation.getPropertyPath().toString().equals("password")) {
                throw new WeakPasswordException();
            }
        }
    }
}
