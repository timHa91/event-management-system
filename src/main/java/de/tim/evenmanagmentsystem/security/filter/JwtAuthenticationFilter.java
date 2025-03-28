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
 * Filter for JWT-based authentication.
 * Checks incoming requests for valid JWTs and sets the authentication context.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    /**
     * Main filter method that is called for each incoming request.
     * Extracts and validates the JWT and sets the authentication context.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // If it's an OPTIONS request (CORS preflight), skip authentication
            if (request.getMethod().equals("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            // For public endpoints, no authentication is required
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract the Authorization header
            final String authHeader = request.getHeader("Authorization");

            log.debug("Auth header: {}", authHeader);

            // If no Authorization header is present or it doesn't start with Bearer,
            // continue with the filter chain without authentication
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No valid Authorization header found");
                filterChain.doFilter(request, response);
                return;
            }

            // Extract the JWT from the header (remove "Bearer ")
            final String jwt = authHeader.substring(7);

            log.debug("Extracted JWT: {}", jwt);

            // Extract the email from the JWT
            final String userEmail = jwtService.extractUsername(jwt);

            log.debug("Extracted user email: {}", userEmail);

            // If the email could be extracted and the user is not yet authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Check if the token exists in the database and is valid
                Optional<Token> tokenOptional = tokenRepository.findByToken(jwt);

                log.debug("Token found in DB: {}", tokenOptional.isPresent());

                if (tokenOptional.isPresent() && tokenOptional.get().isValid()) {
                    // Load user details
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    log.debug("User details loaded: {}", userDetails != null);

                    // If the token is valid
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Create an Authentication object
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // No credentials since we're already authenticated
                                userDetails.getAuthorities() // User permissions
                        );

                        // Add request details
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Set the Authentication context
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("User authenticated: {}", userEmail);
                    } else {
                        log.debug("Invalid JWT for user: {}", userEmail);
                    }
                } else {
                    log.debug("Token not found in database or invalid: {}", jwt);
                }
            }

            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Log the error but let the request continue
            log.error("JWT authentication error", e);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Checks if an endpoint is public and requires no authentication.
     *
     * @param request The HTTP request to check
     * @return true if the endpoint is public, otherwise false
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Auth endpoints are always public
        if (uri.startsWith("/api/auth/")) {
            return true;
        }

        // Swagger/OpenAPI documentation is always public
        if (uri.startsWith("/swagger-ui/") || uri.startsWith("/v3/api-docs")) {
            return true;
        }

        // GET /api/events is public, but POST/PUT/DELETE are not
        if (uri.equals("/api/events") && method.equals("GET")) {
            return true;
        }

        // GET /api/events/{id}/details is public
        if (uri.matches("/api/events/\\d+/details") && method.equals("GET")) {
            return true;
        }

        return false;
    }
}