package de.tim.evenmanagmentsystem.security.config;

import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.User;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void shouldCreateUserDetailsService() {
        // Given
        Attendee attendee = new Attendee();
        attendee.setEmail("test@example.com");
        attendee.setPassword("encodedPassword");
        attendee.setFirstName("Test");
        attendee.setLastName("User");
        attendee.setPhoneNumber("1234567890");
        attendee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        attendee.setAddress(new Address());
        attendee.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(attendee));

        // When
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetailsService);
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When/Then
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void shouldCreateAuthenticationProvider() {
        // When
        AuthenticationProvider authenticationProvider = applicationConfig.authenticationProvider();

        // Then
        assertNotNull(authenticationProvider);
        assertTrue(authenticationProvider instanceof DaoAuthenticationProvider);
    }

    @Test
    void shouldCreatePasswordEncoder() {
        // When
        PasswordEncoder passwordEncoder = applicationConfig.passwordEncoder();

        // Then
        assertNotNull(passwordEncoder);

        // Verify that it can encode and match passwords
        String encoded = passwordEncoder.encode("password");
        assertTrue(passwordEncoder.matches("password", encoded));
        assertFalse(passwordEncoder.matches("wrongpassword", encoded));
    }

    @Test
    void shouldCreateAuthenticationManager() throws Exception {
        // Given
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // When
        AuthenticationManager result = applicationConfig.authenticationManager(authenticationConfiguration);

        // Then
        assertNotNull(result);
        assertSame(authenticationManager, result);

        verify(authenticationConfiguration).getAuthenticationManager();
    }
}