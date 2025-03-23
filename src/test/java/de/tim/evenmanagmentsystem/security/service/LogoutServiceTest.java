package de.tim.evenmanagmentsystem.security.service;

import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private Token token;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void shouldLogoutSuccessfully() {
        // Given
        String jwt = "valid.jwt.token";
        String authHeader = "Bearer " + jwt;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenRepository.findByToken(jwt)).thenReturn(Optional.of(token));

        // When
        logoutService.logout(request, response, authentication);

        // Then
        verify(token).setRevoked(true);
        verify(token).setExpired(true);
        verify(tokenRepository).save(token);
    }

    @Test
    void shouldDoNothingWhenNoAuthHeader() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        logoutService.logout(request, response, authentication);

        // Then
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void shouldDoNothingWhenInvalidAuthHeader() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        // When
        logoutService.logout(request, response, authentication);

        // Then
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void shouldDoNothingWhenTokenNotFound() {
        // Given
        String jwt = "invalid.jwt.token";
        String authHeader = "Bearer " + jwt;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenRepository.findByToken(jwt)).thenReturn(Optional.empty());

        // When
        logoutService.logout(request, response, authentication);

        // Then
        verify(tokenRepository).findByToken(jwt);
        verifyNoMoreInteractions(tokenRepository);
    }
}