package com.th.eventmanagmentsystem.usermanagement.application.registration;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationResponse;
import com.th.eventmanagmentsystem.usermanagement.application.mapper.UserMapper;
import com.th.eventmanagmentsystem.usermanagement.domain.User;
import com.th.eventmanagmentsystem.usermanagement.domain.UserRepository;
import com.th.eventmanagmentsystem.usermanagement.domain.UserRole;
import com.th.eventmanagmentsystem.usermanagement.domain.UserStatus;
import com.th.eventmanagmentsystem.usermanagement.domain.policy.RegistrationPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RegistrationPolicy<UserRegistrationRequest> registrationPolicy;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void whenRegisterNewUser_withNonExistingEmailAndValidPassword_shouldSucceed() {
        // Arrange
        String validPassword = "Password123";
        String nonExistingEmail = "test_user@gmail.com";
        String hashedPassword = "hashedPassword";
        UserStatus userStatus = UserStatus.INACTIVE;
        UserRegistrationRequest request = new UserRegistrationRequest(nonExistingEmail, validPassword);
        User userFromMapper = new User();
        User savedUser = new User();
        UserRegistrationResponse finalResponse = new UserRegistrationResponse(
                "test-uuid",
                nonExistingEmail,
                userStatus
        );

        doNothing().when(registrationPolicy).check(request);
        when(passwordEncoder.encode(validPassword)).thenReturn(hashedPassword);
        when(userMapper.requestToUser(request, hashedPassword, null,
                userStatus, Set.of(UserRole.ROLE_USER))).thenReturn(userFromMapper);
        when(userRepository.save(userFromMapper)).thenReturn(savedUser);
        when(userMapper.userToResponse(savedUser)).thenReturn(finalResponse);

        // Act
        UserRegistrationResponse actualResponse = registerUserUseCase.register(request);

        // Assert
        assertEquals(finalResponse, actualResponse);

        verify(userRepository).save(userFromMapper);
        verify(passwordEncoder).encode(validPassword);
        verify(registrationPolicy).check(request);
        verify(userRepository).save(any(User.class));
    }

}