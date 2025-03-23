package de.tim.evenmanagmentsystem.security.service;

import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service für das Abmelden von Benutzern und das Widerrufen ihrer Tokens.
 * Implementiert das LogoutHandler-Interface von Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Wird aufgerufen, wenn ein Benutzer sich abmeldet.
     * Widerruft das Token, das für die Authentifizierung verwendet wurde.
     *
     * @param request Die HTTP-Anfrage
     * @param response Die HTTP-Antwort
     * @param authentication Die Authentifizierungsinformationen (kann null sein)
     */
    @Override
    @Transactional
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // Extrahiere den Authorization-Header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        // Wenn kein Authorization-Header vorhanden ist oder er nicht mit Bearer beginnt,
        // beende die Methode
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Logout request without valid Authorization header");
            return;
        }

        // Extrahiere das JWT aus dem Header (entferne "Bearer ")
        jwt = authHeader.substring(7);

        log.debug("Processing logout for token: {}", jwt.substring(0, Math.min(10, jwt.length())) + "...");

        // Finde das Token in der Datenbank
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        // Wenn das Token gefunden wurde
        if (storedToken != null) {
            log.debug("Token found in database. Revoking token...");

            // Setze das Token als widerrufen und abgelaufen
            storedToken.setRevoked(true);
            storedToken.setExpired(true);

            // Speichere die Änderungen in der Datenbank
            tokenRepository.save(storedToken);

            log.info("Token successfully revoked");
        } else {
            log.warn("Token not found in database");
        }
    }

    /**
     * Widerruft alle Tokens eines Benutzers.
     * Nützlich bei Passwortänderungen oder Sicherheitsverletzungen.
     *
     * @param userId Die ID des Benutzers
     */
    @Transactional
    public void logoutAllUserSessions(Long userId) {
        log.debug("Revoking all tokens for user ID: {}", userId);

        // Widerrufe alle Tokens des Benutzers
        tokenRepository.revokeAllUserTokens(userId);

        log.info("All tokens for user ID {} successfully revoked", userId);
    }
}