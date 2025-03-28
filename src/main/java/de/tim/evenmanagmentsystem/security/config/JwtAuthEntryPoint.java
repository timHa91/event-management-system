package de.tim.evenmanagmentsystem.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry Point für Authentifizierungsfehler.
 * Wird aufgerufen, wenn ein Benutzer versucht, auf eine geschützte Ressource zuzugreifen,
 * ohne authentifiziert zu sein.
 */
@Component
@Slf4j
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Wird aufgerufen, wenn ein Authentifizierungsfehler auftritt.
     * Sendet eine 401 Unauthorized-Antwort mit einer JSON-Fehlermeldung.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String authHeader = request.getHeader("Authorization");

        String errorMessage;
        String errorCode;
        log.error("TOKEN: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            errorMessage = "Authentication required: No valid token provided";
            errorCode = "NO_TOKEN";
        } else if (authException instanceof InsufficientAuthenticationException) {
            errorMessage = "Authentication failed: Insufficient permissions to access this resource";
            errorCode = "INSUFFICIENT_PERMISSIONS";
        } else {
            errorMessage = "Authentication failed: " + authException.getMessage();
            errorCode = "AUTHENTICATION_FAILED";
        }

        response.getWriter().write("{\"error\":\"" + errorCode + "\",\"message\":\"" + errorMessage + "\",\"path\":\"" + request.getRequestURI() + "\"}");
    }
}