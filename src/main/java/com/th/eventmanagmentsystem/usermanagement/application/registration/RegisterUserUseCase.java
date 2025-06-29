package com.th.eventmanagmentsystem.usermanagement.application.registration;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationResponse;
import com.th.eventmanagmentsystem.usermanagement.application.mapper.UserMapper;
import com.th.eventmanagmentsystem.usermanagement.domain.User;
import com.th.eventmanagmentsystem.usermanagement.domain.UserRepository;
import com.th.eventmanagmentsystem.usermanagement.domain.UserRole;
import com.th.eventmanagmentsystem.usermanagement.domain.UserStatus;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.EmailAlreadyExistsException;
import com.th.eventmanagmentsystem.usermanagement.domain.policy.RegistrationPolicy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RegistrationPolicy<UserRegistrationRequest> userRegistrationPolicy;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository,
                               @Qualifier("defaultUserRegistrationPolicyAggregator")
                               RegistrationPolicy<UserRegistrationRequest> userRegistrationPolicy,
                               UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRegistrationPolicy = userRegistrationPolicy;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserRegistrationResponse register(UserRegistrationRequest request) {
        log.info("Creating new user for email: {}", request.email());

        userRegistrationPolicy.check(request);

        String encodedPassword = passwordEncoder.encode(request.password());
        UserStatus status = UserStatus.INACTIVE;
        Set<UserRole> roles = Set.of(UserRole.ROLE_USER);

        User userToSave = userMapper.requestToUser(
                request,
                encodedPassword,
                null,
                status,
                roles
        );

        try {
            var savedUser = userRepository.save(userToSave);
            return userMapper.userToResponse(savedUser);
        }catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException();
        }
    }
}
