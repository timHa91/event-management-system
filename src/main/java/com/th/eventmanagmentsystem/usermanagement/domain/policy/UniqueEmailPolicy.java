package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.UserRepository;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailPolicy implements RegistrationPolicy<UserRegistrationRequest> {

    private UserRepository userRepository;

    @Override
    public void check(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }
    }
}
