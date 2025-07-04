package com.th.eventmanagmentsystem.usermanagement.application.registration;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationResponse;
import com.th.eventmanagmentsystem.usermanagement.application.mapper.UserMapper;
import com.th.eventmanagmentsystem.usermanagement.domain.model.*;
import com.th.eventmanagmentsystem.usermanagement.domain.repository.UserRepository;
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

    private final String VALID_HASHED_PASSWORD = "$2a$10$N9qo8uLOickgx2ZMRZoMye.IKbeT_IuTL0Fp2aEMavFXrLbpIRH/O";

    @Test
    void whenRegisterFromNewUser_withNonExistingEmailAndValidPassword_shouldSucceed() {
        // Arrange
        String validPassword = "Password123";
        String nonExistingEmail = "test_user@test.com";
        String hashedPasswordString = "hashedPassword";
        UserPassword hashedPassword = UserPassword.of(VALID_HASHED_PASSWORD);
        UserStatus userStatus = UserStatus.INACTIVE;
        UserRegistrationRequest request = new UserRegistrationRequest(nonExistingEmail, validPassword);
        User userFromMapper =  new User(
                EmailAddress.of(nonExistingEmail),
                hashedPassword,
                UserStatus.INACTIVE,
                Set.of(UserRole.ROLE_USER)
        );
        User savedUser = new User(
                EmailAddress.of(nonExistingEmail),
                UserPassword.of(VALID_HASHED_PASSWORD),
                UserStatus.INACTIVE,
                Set.of(UserRole.ROLE_USER)
        );
        UserRegistrationResponse finalResponse = new UserRegistrationResponse(
                "test-uuid",
                nonExistingEmail,
                userStatus
        );

        doNothing().when(registrationPolicy).check(request);
        when(passwordEncoder.encode(validPassword)).thenReturn(hashedPasswordString);
        when(userMapper.toUser(request, hashedPasswordString)).thenReturn(userFromMapper);
        when(userRepository.save(userFromMapper)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(finalResponse);

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
    void whenRegisterFromNewUser_shouldSaveUserWithHashedPasswordAndReturnCorrectResponse() {
        // Arrange
        String validPasswordPlain = "plain-text-password";
        String nonExistingEmail = "test_user@test.com";
        UserStatus userStatus = UserStatus.INACTIVE;
        Set<UserRole> roles = Set.of(UserRole.ROLE_USER);
        UserRegistrationRequest request = new UserRegistrationRequest(nonExistingEmail, validPasswordPlain);
        String expectedHashedPassword = VALID_HASHED_PASSWORD;
        User userFromMapper = new User(EmailAddress.of(nonExistingEmail), UserPassword.of(expectedHashedPassword), userStatus, roles);

        UserRegistrationResponse finalResponse = new UserRegistrationResponse("test-uuid", nonExistingEmail, userStatus);

        when(passwordEncoder.encode(validPasswordPlain)).thenReturn(expectedHashedPassword);
        when(userMapper.toUser(
                eq(request),
                eq(expectedHashedPassword)
        )).thenReturn(userFromMapper);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.toResponse(any(User.class))).thenReturn(finalResponse);

        // Act
        UserRegistrationResponse actualResponse = registerUserUseCase.register(request);

        // Assert
        assertNotNull(actualResponse, "Die zurückgegebene Response sollte nicht null sein.");
        assertEquals(finalResponse, actualResponse);

        verify(userRepository).save(userArgumentCaptor.capture());
        User userPassedToSaveMethod = userArgumentCaptor.getValue();

        assertNotNull(userPassedToSaveMethod);
        assertEquals(expectedHashedPassword, userPassedToSaveMethod.getPassword().hashedPassword());
        assertNotEquals(validPasswordPlain, userPassedToSaveMethod.getPassword());
    }

    @Test
    void whenRegisterFromNewUser_shouldNotSafeWhenEmailAlreadyExits() {
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
        verify(userMapper, never()).toUser(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }
}