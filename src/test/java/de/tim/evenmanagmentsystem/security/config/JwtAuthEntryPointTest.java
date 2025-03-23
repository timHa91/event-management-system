package de.tim.evenmanagmentsystem.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Test
    void shouldReturnUnauthorizedResponse() throws IOException, ServletException {
        // Given
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(authException.getMessage()).thenReturn("Unauthorized");
        when(request.getRequestURI()).thenReturn("/api/users");
        when(response.getWriter()).thenReturn(writer);

        // When
        jwtAuthEntryPoint.commence(request, response, authException);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("\"error\":\"unauthorized\""));
        assertTrue(responseBody.contains("\"message\":\"Unauthorized\""));
        assertTrue(responseBody.contains("\"path\":\"/api/users\""));
    }
}