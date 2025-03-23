package de.tim.evenmanagmentsystem.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
    ) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());

        // Setze den HTTP-Status auf 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Sende eine JSON-Fehlermeldung zurück
        String errorJson = String.format(
                "{\"error\":\"unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                authException.getMessage(),
                request.getRequestURI()
        );

        response.getWriter().write(errorJson);
    }
}