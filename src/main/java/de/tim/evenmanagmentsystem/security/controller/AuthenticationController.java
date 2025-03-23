package de.tim.evenmanagmentsystem.security.controller;

import de.tim.evenmanagmentsystem.security.dto.AuthenticationRequest;
import de.tim.evenmanagmentsystem.security.dto.AuthenticationResponse;
import de.tim.evenmanagmentsystem.security.dto.RefreshTokenRequest;
import de.tim.evenmanagmentsystem.security.dto.RegistrationRequest;
import de.tim.evenmanagmentsystem.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller für Authentifizierungsoperationen.
 * Stellt Endpunkte für Registrierung, Anmeldung, Token-Erneuerung und Abmeldung bereit.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {

    private final AuthenticationService authService;

    /**
     * Registriert einen neuen Teilnehmer.
     *
     * @param request Die Registrierungsanfrage
     * @return Eine Authentifizierungsantwort mit Tokens und Benutzerinformationen
     */
    @PostMapping("/register/attendee")
    @Operation(
            summary = "Register a new attendee",
            description = "Creates a new user account with attendee role and returns authentication tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "409", description = "Email already exists")
            }
    )
    public ResponseEntity<AuthenticationResponse> registerAttendee(
            @Valid @RequestBody RegistrationRequest request
    ) {
        log.info("Registering new attendee with email: {}", request.getEmail());
        AuthenticationResponse response = authService.registerAttendee(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Registriert einen neuen Veranstalter.
     *
     * @param request Die Registrierungsanfrage
     * @return Eine Authentifizierungsantwort mit Tokens und Benutzerinformationen
     */
    @PostMapping("/register/organizer")
    @Operation(
            summary = "Register a new organizer",
            description = "Creates a new user account with organizer role and returns authentication tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "409", description = "Email already exists")
            }
    )
    public ResponseEntity<AuthenticationResponse> registerOrganizer(
            @Valid @RequestBody RegistrationRequest request
    ) {
        log.info("Registering new organizer with email: {}", request.getEmail());
        AuthenticationResponse response = authService.registerOrganizer(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Authentifiziert einen Benutzer.
     *
     * @param request Die Authentifizierungsanfrage
     * @return Eine Authentifizierungsantwort mit Tokens und Benutzerinformationen
     */
    @PostMapping("/login")
    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user with email and password and returns authentication tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @ApiResponse(responseCode = "403", description = "Account disabled")
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        log.info("Authenticating user with email: {}", request.getEmail());
        AuthenticationResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Erneuert ein Access-Token mit einem Refresh-Token.
     *
     * @param request Die Token-Erneuerungsanfrage
     * @return Eine Authentifizierungsantwort mit neuem Access-Token
     */
    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh access token",
            description = "Uses a refresh token to generate a new access token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully refreshed token"),
                    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
            }
    )
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("Refreshing token");
        AuthenticationResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Meldet einen Benutzer ab und widerruft sein Token.
     *
     * @param request Die HTTP-Anfrage mit dem Authorization-Header
     * @return Eine leere Antwort mit HTTP-Status 204 (No Content)
     */
    @PostMapping("/logout")
    @Operation(
            summary = "Logout a user",
            description = "Invalidates the current authentication token",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully logged out")
            }
    )
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.info("Logging out user");
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }
}