package de.tim.evenmanagmentsystem.security.service;

import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.security.dto.AuthenticationRequest;
import de.tim.evenmanagmentsystem.security.dto.AuthenticationResponse;
import de.tim.evenmanagmentsystem.security.dto.RefreshTokenRequest;
import de.tim.evenmanagmentsystem.security.dto.RegistrationRequest;
import de.tim.evenmanagmentsystem.security.exception.AccountDisabledException;
import de.tim.evenmanagmentsystem.security.exception.EmailAlreadyExistsException;
import de.tim.evenmanagmentsystem.security.exception.InvalidTokenException;
import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.model.TokenType;
import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import de.tim.evenmanagmentsystem.user.repository.AttendeeRepository;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttendeeRepository attendeeRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegistrationRequest attendeeRegistrationRequest;
    private RegistrationRequest organizerRegistrationRequest;
    private AuthenticationRequest authenticationRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private UserDetails userDetails;
    private Attendee attendee;
    private Organizer organizer;
    private final String accessToken = "access.token.string";
    private final String refreshToken = "refresh.token.string";

    @BeforeEach
    void setUp() {
        // Setup für Attendee-Registrierung
        attendeeRegistrationRequest = new RegistrationRequest();
        attendeeRegistrationRequest.setEmail("attendee@example.com");
        attendeeRegistrationRequest.setPassword("password123");
        attendeeRegistrationRequest.setFirstName("John");
        attendeeRegistrationRequest.setLastName("Doe");
        attendeeRegistrationRequest.setPhoneNumber("1234567890");
        attendeeRegistrationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));

        // Setup für Organizer-Registrierung
        organizerRegistrationRequest = new RegistrationRequest();
        organizerRegistrationRequest.setEmail("organizer@example.com");
        organizerRegistrationRequest.setPassword("password123");
        organizerRegistrationRequest.setFirstName("Jane");
        organizerRegistrationRequest.setLastName("Smith");
        organizerRegistrationRequest.setOrganizationName("Test Organization");
        organizerRegistrationRequest.setCompanyRegistrationNumber("1234567890");
        organizerRegistrationRequest.setBankAccountInfo("1234567890");
        organizerRegistrationRequest.setDescription("Test Description");

        // Setup für Authentifizierung
        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("user@example.com");
        authenticationRequest.setPassword("password123");

        // Setup für Token-Erneuerung
        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(refreshToken);

        // UserDetails für Tests
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user@example.com")
                .password("encodedPassword")
                .authorities("ROLE_ATTENDEE")
                .build();

        // Attendee-Entität
        attendee = new Attendee();
        attendee.setId(1L);
        attendee.setEmail("attendee@example.com");
        attendee.setPassword("encodedPassword");
        attendee.setFirstName("John");
        attendee.setLastName("Doe");
        attendee.setActive(true);
        attendee.addRole(UserRole.ROLE_ATTENDEE);

        // Organizer-Entität
        organizer = new Organizer();
        organizer.setId(2L);
        organizer.setEmail("organizer@example.com");
        organizer.setPassword("encodedPassword");
        organizer.setFirstName("Jane");
        organizer.setLastName("Smith");
        organizer.setOrganizationName("Test Organization");
        organizer.setActive(true);
        organizer.addRole(UserRole.ROLE_ORGANIZER);
    }

    @Test
    void shouldRegisterAttendee() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(attendeeRepository.save(any(Attendee.class))).thenReturn(attendee);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(refreshToken);
        when(jwtService.extractExpirationAsLocalDateTime(anyString())).thenReturn(LocalDateTime.now().plusDays(1));

        // When
        AuthenticationResponse response = authenticationService.registerAttendee(attendeeRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals("ATTENDEE", response.getUserType());

        verify(userRepository).existsByEmail("attendee@example.com");
        verify(passwordEncoder).encode("password123");

        ArgumentCaptor<Attendee> attendeeCaptor = ArgumentCaptor.forClass(Attendee.class);
        verify(attendeeRepository).save(attendeeCaptor.capture());
        Attendee savedAttendee = attendeeCaptor.getValue();
        assertEquals("attendee@example.com", savedAttendee.getEmail());
        assertEquals("encodedPassword", savedAttendee.getPassword());
        assertTrue(savedAttendee.isActive());
        assertTrue(savedAttendee.getRoles().contains(UserRole.ROLE_ATTENDEE));

        verify(userDetailsService).loadUserByUsername("attendee@example.com");
        verify(jwtService).generateToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        Token savedToken = tokenCaptor.getValue();
        assertEquals(accessToken, savedToken.getToken());
        assertEquals(TokenType.BEARER, savedToken.getTokenType());
        assertFalse(savedToken.isExpired());
        assertFalse(savedToken.isRevoked());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringWithExistingEmail() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authenticationService.registerAttendee(attendeeRegistrationRequest);
        });

        verify(userRepository).existsByEmail("attendee@example.com");
        verifyNoMoreInteractions(passwordEncoder, attendeeRepository, userDetailsService, jwtService, tokenRepository);
    }

    @Test
    void shouldRegisterOrganizer() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(organizerRepository.save(any(Organizer.class))).thenReturn(organizer);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(refreshToken);
        when(jwtService.extractExpirationAsLocalDateTime(anyString())).thenReturn(LocalDateTime.now().plusDays(1));

        // When
        AuthenticationResponse response = authenticationService.registerOrganizer(organizerRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals("ORGANIZER", response.getUserType());

        verify(userRepository).existsByEmail("organizer@example.com");
        verify(passwordEncoder).encode("password123");

        ArgumentCaptor<Organizer> organizerCaptor = ArgumentCaptor.forClass(Organizer.class);
        verify(organizerRepository).save(organizerCaptor.capture());
        Organizer savedOrganizer = organizerCaptor.getValue();
        assertEquals("organizer@example.com", savedOrganizer.getEmail());
        assertEquals("encodedPassword", savedOrganizer.getPassword());
        assertEquals("Test Organization", savedOrganizer.getOrganizationName());
        assertTrue(savedOrganizer.isActive());
        assertTrue(savedOrganizer.getRoles().contains(UserRole.ROLE_ORGANIZER));

        verify(userDetailsService).loadUserByUsername("organizer@example.com");
        verify(jwtService).generateToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);
    }

    @Test
    void shouldAuthenticateUser() {
        // Given
        String userEmail = "user@example.com";
        String userPassword = "password123";

        // Setup AuthenticationRequest
        authenticationRequest.setEmail(userEmail);
        authenticationRequest.setPassword(userPassword);

        // Setup Attendee mit konsistenter E-Mail
        Attendee attendee = new Attendee();
        attendee.setId(3L);
        attendee.setEmail(userEmail);
        attendee.setPassword("encodedPassword");
        attendee.setFirstName("Test");
        attendee.setLastName("Attendee");
        attendee.setPhoneNumber("1234567890");
        attendee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        attendee.setAddress(new Address());
        attendee.setActive(true);
        attendee.addRole(UserRole.ROLE_ATTENDEE);

        // Setup UserDetails
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername(userEmail)
                .password("encodedPassword")
                .authorities("ROLE_ATTENDEE")
                .build();

        // Konfiguriere den authentication-Mock
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Konfiguriere weitere Mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(attendee));
        when(jwtService.generateToken(userDetails)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(userDetails)).thenReturn(refreshToken);
        when(jwtService.extractExpirationAsLocalDateTime(anyString()))
                .thenReturn(LocalDateTime.now().plusDays(1));
        when(tokenRepository.findAllValidTokensByUser(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        // Then
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());

        verify(tokenRepository).findAllValidTokensByUser(eq(3L), any(LocalDateTime.class));
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(userEmail, userPassword));
        verify(userRepository).findByEmail(userEmail);
        verify(tokenRepository).findAllValidTokensByUser(eq(3L), any(LocalDateTime.class));
        verify(jwtService).generateToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);

    }

    @Test
    void shouldThrowExceptionWhenAuthenticatingInactiveUser() {
        // Given
        Attendee attendee = new Attendee();
        attendee.setId(3L);
        attendee.setEmail("test.attendee@example.com");
        attendee.setPassword("encodedPassword");
        attendee.setFirstName("Test");
        attendee.setLastName("Attendee");
        attendee.setPhoneNumber("1234567890");
        attendee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        attendee.setAddress(new Address());
        attendee.setActive(false);
        attendee.addRole(UserRole.ROLE_ATTENDEE);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(attendee));

        // When/Then
        assertThrows(AccountDisabledException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("user@example.com", "password123"));
        verify(userRepository).findByEmail("user@example.com");
        verifyNoMoreInteractions(jwtService, tokenRepository);
    }

    @Test
    void shouldRefreshToken() {
        // Given
        Attendee attendee = new Attendee();
        attendee.setId(3L);
        attendee.setEmail("test.attendee@example.com");
        attendee.setPassword("encodedPassword");
        attendee.setFirstName("Test");
        attendee.setLastName("Attendee");
        attendee.setPhoneNumber("1234567890");
        attendee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        attendee.setAddress(new Address());
        attendee.setActive(true);
        attendee.addRole(UserRole.ROLE_ATTENDEE);

        when(jwtService.extractUsername(refreshToken)).thenReturn("user@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(attendee));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(accessToken);
        when(jwtService.extractExpirationAsLocalDateTime(anyString())).thenReturn(LocalDateTime.now().plusDays(1));
        when(tokenRepository.findAllValidTokensByUser(anyLong(), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        // When
        AuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);

        // Then
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());

        verify(jwtService).extractUsername(refreshToken);
        verify(userRepository).findByEmail("user@example.com");
        verify(userDetailsService).loadUserByUsername("user@example.com");
        verify(jwtService).isTokenValid(refreshToken, userDetails);
        verify(tokenRepository).findAllValidTokensByUser(eq(3L), any(LocalDateTime.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void shouldThrowExceptionWhenRefreshingWithInvalidToken() {
        // Given
        when(jwtService.extractUsername(refreshToken)).thenReturn(null);

        // When/Then
        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.refreshToken(refreshTokenRequest);
        });

        verify(jwtService).extractUsername(refreshToken);
        verifyNoMoreInteractions(userRepository, userDetailsService, jwtService, tokenRepository);
    }

    @Test
    void shouldThrowExceptionWhenRefreshingWithInactiveUser() {
        // Given
        var user = new Attendee();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setActive(false); // Inaktiver Benutzer

        when(jwtService.extractUsername(refreshToken)).thenReturn("user@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When/Then
        assertThrows(AccountDisabledException.class, () -> {
            authenticationService.refreshToken(refreshTokenRequest);
        });

        verify(jwtService).extractUsername(refreshToken);
        verify(userRepository).findByEmail("user@example.com");
        verifyNoMoreInteractions(userDetailsService, jwtService, tokenRepository);
    }

    @Test
    void shouldLogoutSuccessfully() {
        // Given
        String validToken = "valid.token";
        String authHeader = "Bearer " + validToken;

        Token token = new Token();
        token.setToken(validToken);

        when(tokenRepository.findByToken(validToken)).thenReturn(Optional.of(token));

        // When
        authenticationService.logout(authHeader);

        // Then
        verify(tokenRepository).findByToken(validToken);
        verify(tokenRepository).save(token);
        assertTrue(token.isRevoked());
        assertTrue(token.isExpired());
    }

    @Test
    void shouldHandleNullAuthHeaderInLogout() {
        // When
        authenticationService.logout(null);

        // Then
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void shouldHandleInvalidAuthHeaderInLogout() {
        // When
        authenticationService.logout("InvalidHeader");

        // Then
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void shouldHandleTokenNotFoundInLogout() {
        // Given
        String validToken = "valid.token";
        String authHeader = "Bearer " + validToken;

        when(tokenRepository.findByToken(validToken)).thenReturn(Optional.empty());

        // When
        authenticationService.logout(authHeader);

        // Then
        verify(tokenRepository).findByToken(validToken);
        verifyNoMoreInteractions(tokenRepository);
    }
}