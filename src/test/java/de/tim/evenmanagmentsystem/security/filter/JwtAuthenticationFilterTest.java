package de.tim.evenmanagmentsystem.security.filter;

import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Token token;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;
    private final String validToken = "valid.jwt.token";
    private final String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // Stelle sicher, dass der SecurityContext leer ist

        userDetails = User.builder()
                .username(userEmail)
                .password("password")
                .authorities("ROLE_ATTENDEE")
                .build();
    }

    @Test
    void shouldSkipFilterForOptionsRequest() throws ServletException, IOException {
        // Given
        when(request.getMethod()).thenReturn("OPTIONS");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository);
    }

    @Test
    void shouldSkipFilterWhenNoAuthHeader() throws ServletException, IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository);
    }

    @Test
    void shouldSkipFilterWhenInvalidAuthHeader() throws ServletException, IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository);
    }

    @Test
    void shouldAuthenticateWithValidToken() throws ServletException, IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        when(jwtService.extractUsername(validToken)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(tokenRepository.findByToken(validToken)).thenReturn(Optional.of(token));
        when(token.isValid()).thenReturn(true);
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verify(userDetailsService).loadUserByUsername(userEmail);
        verify(tokenRepository).findByToken(validToken);
        verify(jwtService).isTokenValid(validToken, userDetails);

        // Überprüfe, ob der SecurityContext gesetzt wurde
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userEmail, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void shouldNotAuthenticateWithInvalidToken() throws ServletException, IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        when(jwtService.extractUsername(validToken)).thenReturn(userEmail);
        when(tokenRepository.findByToken(validToken)).thenReturn(Optional.of(token));
        when(token.isValid()).thenReturn(false); // Token ist nicht gültig

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verify(tokenRepository).findByToken(validToken);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        // Überprüfe, ob der SecurityContext leer ist
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotAuthenticateWhenTokenNotFound() throws ServletException, IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        when(jwtService.extractUsername(validToken)).thenReturn(userEmail);
        when(tokenRepository.findByToken(validToken)).thenReturn(Optional.empty()); // Token nicht in DB gefunden

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verify(tokenRepository).findByToken(validToken);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        // Überprüfe, ob der SecurityContext leer ist
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldHandleExceptionGracefully() throws ServletException, IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        when(jwtService.extractUsername(validToken)).thenThrow(new RuntimeException("Test exception"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);

        // Überprüfe, ob der SecurityContext leer ist
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}