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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void whenRegisterNewUser_withNonExistingEmailAndValidPassword_shouldSucceed() {
        // Arrange
        String validPassword = "Password123";
        String nonExistingEmail = "test_user@test.com";
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

    @Test
    void whenRegisterNewUser_shouldSaveUserWithHashedPasswordAndReturnCorrectResponse() {
        // Arrange
        String validPasswordPlain = "plain-text-password";
        String nonExistingEmail = "test_user@test.com";
        UserStatus userStatus = UserStatus.INACTIVE;
        Set<UserRole> roles = Set.of(UserRole.ROLE_USER);
        UserRegistrationRequest request = new UserRegistrationRequest(nonExistingEmail, validPasswordPlain);
        String expectedHashedPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMye.IKbeT_IuTL0Fp2aEMavFXrLbpIRH/O";
        User userFromMapper = new User(nonExistingEmail, expectedHashedPassword, userStatus, roles, null);

        UserRegistrationResponse finalResponse = new UserRegistrationResponse("test-uuid", nonExistingEmail, userStatus);

        when(passwordEncoder.encode(validPasswordPlain)).thenReturn(expectedHashedPassword);
        when(userMapper.requestToUser(
                eq(request),
                eq(expectedHashedPassword),
                isNull(),
                eq(userStatus),
                eq(roles)
        )).thenReturn(userFromMapper);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.userToResponse(any(User.class))).thenReturn(finalResponse);

        // Act
        UserRegistrationResponse actualResponse = registerUserUseCase.register(request);

        // Assert
        assertNotNull(actualResponse, "Die zurückgegebene Response sollte nicht null sein.");
        assertEquals(finalResponse, actualResponse);

        verify(userRepository).save(userArgumentCaptor.capture());
        User userPassedToSaveMethod = userArgumentCaptor.getValue();

        assertNotNull(userPassedToSaveMethod);
        assertEquals(expectedHashedPassword, userPassedToSaveMethod.getPassword());
        assertNotEquals(validPasswordPlain, userPassedToSaveMethod.getPassword());
    }

    @Test
    void whenRegisterNewUser_shouldNotSafeWhenEmailAlreadyExits() {
        UserRegistrationRequest requestWithDuplicateEmail = new UserRegistrationRequest(
                "duplicate@example.com",
                "ValidPassword123!"
        );
        String expectedErrorMessage = "Die E-Mail duplicate@example.com ist bereits vergeben.";

        doThrow(new EmailAlreadyExistsException(expectedErrorMessage))
                .when(registrationPolicy)
                .check(requestWithDuplicateEmail);
        EmailAlreadyExistsException thrownException = assertThrows(EmailAlreadyExistsException.class, () -> {
            registerUserUseCase.register(requestWithDuplicateEmail);
        });

        assertEquals(expectedErrorMessage, thrownException.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).requestToUser(any(), any(), any(), any(), any());
        verify(userRepository, never()).save(any(User.class));
    }
}