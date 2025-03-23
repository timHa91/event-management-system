package de.tim.evenmanagmentsystem.security.service;

import de.tim.evenmanagmentsystem.security.dto.AuthenticationRequest;
import de.tim.evenmanagmentsystem.security.dto.AuthenticationResponse;
import de.tim.evenmanagmentsystem.security.dto.RefreshTokenRequest;
import de.tim.evenmanagmentsystem.security.exception.EmailAlreadyExistsException;
import de.tim.evenmanagmentsystem.security.exception.InvalidTokenException;
import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.model.TokenType;
import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.user.dto.AttendeeRegistrationDTO;
import de.tim.evenmanagmentsystem.user.dto.OrganizerRegistrationDTO;
import de.tim.evenmanagmentsystem.user.model.*;
import de.tim.evenmanagmentsystem.user.repository.AttendeeRepository;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AttendeeRepository attendeeRepository;
    private final OrganizerRepository organizerRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Registriert einen neuen Teilnehmer (Attendee).
     *
     * @param request Die Registrierungsdaten
     * @return Eine Antwort mit Access- und Refresh-Token
     * @throws EmailAlreadyExistsException Wenn die E-Mail bereits verwendet wird
     */
    @Transactional
    public AuthenticationResponse registerAttendee(@Valid AttendeeRegistrationDTO request) {
        log.info("Registering new attendee with email: {}", request.getEmail());

        // Prüfe, ob E-Mail bereits existiert
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exits: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // Erstelle Attendee-Entität
        var attendee = new Attendee(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getDateOfBirth(),
                request.getAddress(),
                request.getCity(),
                request.getPostalCode(),
                request.getCountry()
        );
        attendee.setUserStatus(UserStatus.ACTIVE);
        attendee.setActive(true);

        attendee.addRole(UserRole.ROLE_USER);
        attendee.addRole(UserRole.ROLE_ATTENDEE);

        // Speichere Attendee
        var savedAttendee = attendeeRepository.save(attendee);
        log.debug("Attendee saved with ID: {}", savedAttendee.getId());

        // Generiere Tokens
        var jwtToken = generateAccessToken(savedAttendee);
        var refreshToken = generateRefreshToken(savedAttendee);

        log.info("Attendee registered successfully: {}", savedAttendee.getEmail());

        // Erstelle und gib die Antwort zurück
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userType("ATTENDEE")
                .userId(savedAttendee.getId())
                .email(savedAttendee.getEmail())
                .firstName(savedAttendee.getFirstName())
                .lastName(savedAttendee.getLastName())
                .build();
    }

    /**
     * Registriert einen neuen Veranstalter (Organizer).
     *
     * @param request Die Registrierungsdaten
     * @return Eine Antwort mit Access- und Refresh-Token
     * @throws EmailAlreadyExistsException Wenn die E-Mail bereits verwendet wird
     */
    @Transactional
    public AuthenticationResponse registerOrganizer(@Valid OrganizerRegistrationDTO request) {
        log.info("Registering new organizer with email: {}", request.getEmail());

        // Prüfe, ob E-Mail bereits existiert
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // Erstelle Organizer-Entität
        var organizer = new Organizer(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getOrganizationName(),
                request.getDescription(),
                request.getCompanyRegistrationNumber(),
                request.getBankAccountInfo()
        );

        organizer.setUserStatus(UserStatus.ACTIVE);
        organizer.setActive(true);

        organizer.addRole(UserRole.ROLE_USER);
        organizer.addRole(UserRole.ROLE_ORGANIZER);

        var savedOrganizer = organizerRepository.save(organizer);
        log.debug("Organizer saved with ID: {}", savedOrganizer.getId());

        // Generiere Tokens
        var jwtToken = generateAccessToken(savedOrganizer);
        var refreshToken = generateRefreshToken(savedOrganizer);

        log.info("Organizer registered successfully: {}", savedOrganizer.getEmail());

        // Erstelle und gib die Antwort zurück
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userType("ORGANIZER")
                .userId(savedOrganizer.getId())
                .email(savedOrganizer.getEmail())
                .firstName(savedOrganizer.getFirstName())
                .lastName(savedOrganizer.getLastName())
                .organizationName(savedOrganizer.getOrganizationName())
                .build();
    }


    /**
     * Authentifiziert einen Benutzer und generiert neue Tokens.
     *
     * @param request Die Authentifizierungsdaten (E-Mail und Passwort)
     * @return Eine Antwort mit Access- und Refresh-Token
     * @throws BadCredentialsException Wenn die Anmeldedaten ungültig sind
     * @throws UsernameNotFoundException Wenn der Benutzer nicht gefunden wird
     */
    @Transactional
    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        log.info("Authenticating user: {}", request.getEmail());

        try {
            // Authentifiziere Benutzer mit Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            log.debug("Authentication successful for user: {}", request.getEmail());

            // Lade Benutzer
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Überprüfe, ob der Benutzer aktiv ist
            var userDetailsChecker = new AccountStatusUserDetailsChecker();
            userDetailsChecker.check(createUserDetails(user));

            // Bestimme Benutzertyp
            String userType = getUserType(user);

            // Widerrufe alte Tokens
            revokeAllUserTokens(user);

            // Generiere neue Tokens
            var accessToken = generateAccessToken(user);
            var refreshToken = generateRefreshToken(user);

            log.info("User authenticated successfully: {}", user.getEmail());

            // Erstelle und gib die Antwort zurück
            AuthenticationResponse.AuthenticationResponseBuilder responseBuilder = AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userType(userType)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName());

            // Füge organisationName hinzu, wenn es sich um einen Organizer handelt
            if (user instanceof Organizer) {
                responseBuilder.organizationName(((Organizer) user).getOrganizationName());
            }

            return responseBuilder.build();
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", request.getEmail());
            throw e;
        }
    }

    /**
     * Erneuert ein Access-Token mit einem gültigen Refresh-Token.
     *
     * @param request Der Refresh-Token-Request
     * @return Eine Antwort mit neuem Access-Token und dem bestehenden Refresh-Token
     * @throws InvalidTokenException Wenn der Refresh-Token ungültig ist
     * @throws UsernameNotFoundException Wenn der Benutzer nicht gefunden wird
     */
    @Transactional
    public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest request) {
        log.info("Processing refresh token request");

        // Extrahiere Refresh-Token
        String refreshToken = request.getRefreshToken();

        // Extrahiere Benutzername aus Token
        String userEmail;
        try {
            userEmail = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token");
        }

        if (userEmail == null) {
            log.warn("No username found in refresh token");
            throw new InvalidTokenException("Invalid refresh token");
        }

        log.debug("Extracted email from refresh token: {}", userEmail);

        // Lade Benutzer
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", userEmail);
                    return new UsernameNotFoundException("User not found");
                });

        // Lade Benutzerdetails
        UserDetails userDetails = createUserDetails(user);

        // Prüfe Token-Gültigkeit
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            log.warn("Refresh token is invalid or expired");
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Prüfe, ob das Token in der Datenbank existiert und gültig ist
        boolean isTokenValid = tokenRepository.findByToken(refreshToken)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);

        if (!isTokenValid) {
            log.warn("Refresh token not found in database or revoked");
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Bestimme Benutzertyp
        String userType = getUserType(user);

        // Generiere neues Access-Token
        var accessToken = generateAccessToken(user);

        log.info("Token refreshed successfully for user: {}", user.getEmail());

        // Erstelle und gib die Antwort zurück
        AuthenticationResponse.AuthenticationResponseBuilder responseBuilder = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken) // Gib das gleiche Refresh-Token zurück
                .userType(userType)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName());

        // Füge organisationName hinzu, wenn es sich um einen Organizer handelt
        if (user instanceof Organizer) {
            responseBuilder.organizationName(((Organizer) user).getOrganizationName());
        }

        return responseBuilder.build();
    }

    /**
     * Generiert ein Access-Token für einen Benutzer und speichert es in der Datenbank.
     *
     * @param user Der Benutzer
     * @return Das generierte Access-Token
     */
    private String generateAccessToken(User user) {
        UserDetails userDetails = createUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);

        // Berechne das Ablaufdatum
        LocalDateTime expiresAt = jwtService.extractExpirationAsLocalDateTime(jwtToken);

        // Speichere das Token in der Datenbank
        saveUserToken(user, jwtToken, TokenType.BEARER, expiresAt);

        return jwtToken;
    }

    /**
     * Generiert ein Refresh-Token für einen Benutzer und speichert es in der Datenbank.
     *
     * @param user Der Benutzer
     * @return Das generierte Refresh-Token
     */
    private String generateRefreshToken(User user) {
        UserDetails userDetails = createUserDetails(user);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Berechne das Ablaufdatum
        LocalDateTime expiresAt = jwtService.extractExpirationAsLocalDateTime(refreshToken);

        // Speichere das Token in der Datenbank
        saveUserToken(user, refreshToken, TokenType.REFRESH, expiresAt);

        return refreshToken;
    }

    /**
     * Speichert ein Token in der Datenbank.
     *
     * @param user Der Benutzer
     * @param jwtToken Das JWT
     * @param tokenType Der Token-Typ
     * @param expiresAt Das Ablaufdatum
     */
    private void saveUserToken(User user, String jwtToken, TokenType tokenType, LocalDateTime expiresAt) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(token);
        log.debug("Token saved to database: type={}, expires={}", tokenType, expiresAt);
    }

    /**
     * Widerruft alle gültigen Tokens eines Benutzers.
     *
     * @param user Der Benutzer
     */
    private void revokeAllUserTokens(User user) {
        log.debug("Revoking all valid tokens for user: {}", user.getEmail());
        tokenRepository.revokeAllUserTokens(user.getId());
    }

    /**
     * Erstellt UserDetails für einen Benutzer.
     *
     * @param user Der Benutzer
     * @return Die UserDetails
     */
    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getEncodedPasswordForAuthentication())
                .roles(user.getRoles().stream()
                        .map(role -> role.name().substring(5)) // Entferne "ROLE_" Präfix
                        .toArray(String[]::new))
                .accountExpired(!user.isActive())
                .accountLocked(!user.isActive())
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }

    /**
     * Bestimmt den Typ eines Benutzers.
     *
     * @param user Der Benutzer
     * @return Der Benutzertyp als String
     */
    private String getUserType(User user) {
        if (user instanceof Attendee) {
            return "ATTENDEE";
        } else if (user instanceof Organizer) {
            return "ORGANIZER";
        } else {
            return "USER";
        }
    }
}
