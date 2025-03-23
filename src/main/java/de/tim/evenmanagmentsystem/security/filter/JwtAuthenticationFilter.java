package de.tim.evenmanagmentsystem.security.filter;

import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter für die JWT-basierte Authentifizierung.
 * Überprüft eingehende Anfragen auf gültige JWTs und setzt den Authentifizierungskontext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    /**
     * Hauptmethode des Filters, die für jede eingehende Anfrage aufgerufen wird.
     * Extrahiert und validiert das JWT und setzt den Authentifizierungskontext.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Wenn es ein OPTIONS-Request ist (CORS preflight), überspringen wir die Authentifizierung
            if (request.getMethod().equals("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Für öffentliche Endpunkte ist keine Authentifizierung erforderlich
            if (isPublicEndpoint(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extrahiere den Authorization-Header
            final String authHeader = request.getHeader("Authorization");

            // Wenn kein Authorization-Header vorhanden ist oder er nicht mit Bearer beginnt,
            // fahre mit der Filterkette fort ohne Authentifizierung
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extrahiere das JWT aus dem Header (entferne "Bearer ")
            final String jwt = authHeader.substring(7);

            // Extrahiere die E-Mail aus dem JWT
            final String userEmail = jwtService.extractUsername(jwt);

            // Wenn die E-Mail extrahiert werden konnte und der Benutzer noch nicht authentifiziert ist
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Prüfe, ob das Token in der Datenbank existiert und gültig ist
                Optional<Token> tokenOptional = tokenRepository.findByToken(jwt);

                if (tokenOptional.isPresent() && tokenOptional.get().isValid()) {
                    // Lade die Benutzerdetails
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    // Wenn das Token gültig ist
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Erstelle ein Authentication-Objekt
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Keine Credentials, da wir bereits authentifiziert sind
                                userDetails.getAuthorities() // Berechtigungen des Benutzers
                        );

                        // Füge Request-Details hinzu
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Setze den Authentication-Kontext
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("User authenticated: {}", userEmail);
                    } else {
                        log.debug("Invalid JWT for user: {}", userEmail);
                    }
                } else {
                    log.debug("Token not found in database or invalid: {}", jwt);
                }
            }

            // Fahre mit der Filterkette fort
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Logge den Fehler, aber lass die Anfrage weiterlaufen
            log.error("JWT authentication error", e);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Überprüft, ob ein Endpunkt öffentlich ist und keine Authentifizierung erfordert.
     *
     * @param uri Der zu überprüfende URI
     * @return true, wenn der Endpunkt öffentlich ist, sonst false
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/") ||
                uri.equals("/api/events") ||
                uri.matches("/api/events/\\d+/details") ||
                uri.startsWith("/swagger-ui/") ||
                uri.startsWith("/v3/api-docs");
    }
}