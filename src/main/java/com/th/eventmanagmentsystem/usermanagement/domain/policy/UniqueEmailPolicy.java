package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.model.EmailAddress;
import com.th.eventmanagmentsystem.usermanagement.domain.repository.UserRepository;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailPolicy implements RegistrationPolicy<UserRegistrationRequest> {

    private final UserRepository userRepository;

    @Override
    public void check(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(EmailAddress.of(request.email()))) {
            throw new EmailAlreadyExistsException();
        }
    }
}
