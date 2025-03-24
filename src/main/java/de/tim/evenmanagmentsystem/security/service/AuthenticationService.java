package de.tim.evenmanagmentsystem.security.service;

import de.tim.evenmanagmentsystem.security.dto.AuthenticationRequest;
import de.tim.evenmanagmentsystem.security.dto.AuthenticationResponse;
import de.tim.evenmanagmentsystem.security.dto.RefreshTokenRequest;
import de.tim.evenmanagmentsystem.security.dto.RegistrationRequest;
import de.tim.evenmanagmentsystem.security.exception.AccountDisabledException;
import de.tim.evenmanagmentsystem.security.exception.AccountLockedException;
import de.tim.evenmanagmentsystem.security.exception.EmailAlreadyExistsException;
import de.tim.evenmanagmentsystem.security.exception.InvalidTokenException;
import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.model.TokenType;
import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.model.User;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import de.tim.evenmanagmentsystem.user.repository.AttendeeRepository;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Service für Authentifizierungsoperationen.
 * Implementiert die Geschäftslogik für Registrierung, Anmeldung, Token-Erneuerung und Abmeldung.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AttendeeRepository attendeeRepository;
    private final OrganizerRepository organizerRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;

    /**
     * Registriert einen neuen Teilnehmer.
     *
     * @param request Die Registrierungsanfrage
     * @return Eine Authentifizierungsantwort mit Tokens und Benutzerinformationen
     * @throws EmailAlreadyExistsException wenn die E-Mail-Adresse bereits verwendet wird
     */
    @Transactional
    public AuthenticationResponse registerAttendee(RegistrationRequest request) {
        // Überprüfe, ob die E-Mail bereits verwendet wird
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        // Validiere attendee-spezifische Felder
        validateAttendeeFields(request);

        // Erstelle Attendee-Entität
        Attendee attendee = new Attendee();
        attendee.setEmail(request.getEmail());
        attendee.setPassword(passwordEncoder.encode(request.getPassword()));
        attendee.setFirstName(request.getFirstName());
        attendee.setLastName(request.getLastName());
        attendee.setPhoneNumber(request.getPhoneNumber());
        attendee.setDateOfBirth(request.getDateOfBirth());
        attendee.setAddress(request.getAddress());
        attendee.setCity(request.getCity());
        attendee.setPostalCode(request.getPostalCode());
        attendee.setCountry(request.getCountry());

        // Setze Standardwerte
        attendee.setActive(true);

        // Setze Rollen
        attendee.addRole(UserRole.ROLE_USER);
        attendee.addRole(UserRole.ROLE_ATTENDEE);

        // Speichere Attendee
        attendee = attendeeRepository.save(attendee);

        // Generiere Tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(attendee.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Speichere Access-Token in der Datenbank
        saveToken(attendee, accessToken, TokenType.BEARER, jwtService.extractExpirationAsLocalDateTime(accessToken));

        // Erstelle und gib Antwort zurück
        return buildAuthResponse(attendee, accessToken, refreshToken);
    }

    /**
     * Registriert einen neuen Veranstalter.
     *
     * @param request Die Registrierungsanfrage
     * @return Eine Authentifizierungsantwort mit Tokens und Benutzerinformationen
     * @throws EmailAlreadyExistsException wenn die E-Mail-Adresse bereits verwendet wird
     */
    @Transactional
    public AuthenticationResponse registerOrganizer(RegistrationRequest request) {
        // Überprüfe, ob die E-Mail bereits verwendet wird
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        // Validiere organizer-spezifische Felder
        validateOrganizerFields(request);

        // Erstelle Organizer-Entität
        Organizer organizer = new Organizer();
        organizer.setEmail(request.getEmail());
        organizer.setPassword(passwordEncoder.encode(request.getPassword()));
        organizer.setFirstName(request.getFirstName());
        organizer.setLastName(request.getLastName());
        organizer.setOrganizationName(request.getOrganizationName());
        organizer.setDescription(request.getDescription());
        organizer.setContactPhone(request.getContactPhone());
        organizer.setWebsite(request.getWebsite());

        // Setze Standardwerte
        organizer.setActive(true);

        // Setze Rollen
        organizer.addRole(UserRole.ROLE_USER);
        organizer.addRole(UserRole.ROLE_ORGANIZER);

        // Speichere Organizer
        organizer = organizerRepository.save(organizer);

        // Generiere Tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(organizer.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Speichere Access-Token in der Datenbank
        saveToken(organizer, accessToken, TokenType.BEARER, jwtService.extractExpirationAsLocalDateTime(accessToken));

        // Erstelle und gib Antwort zurück
        return buildAuthResponse(organizer, accessToken, refreshToken);
    }

    /**
     * Authentifiziert einen Benutzer.
     *
     * @param request Die Authentifizierungsanfrage
     * @return Eine Authentifizierungsantwort mit Tokens und Benutzerinformationen
     * @throws org.springframework.security.authentication.BadCredentialsException wenn die Anmeldedaten ungültig sind
     * @throws AccountDisabledException wenn das Konto deaktiviert ist
     */
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Überprüfe, ob die E-Mail-Adresse gesperrt ist
        if (loginAttemptService.isBlocked(request.getEmail())) {
            throw new AccountLockedException("Account is locked due to too many failed login attempts. Please try again later.");
        }

        try {
            // Authentifiziere Benutzer mit Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Lade Benutzer aus der Datenbank
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

            // Überprüfe, ob der Benutzer aktiv ist
            if (!user.isActive()) {
                throw new AccountDisabledException("Account is disabled");
            }

            // Widerrufe alle existierenden Tokens
            revokeAllUserTokens(user);

            // Generiere neue Tokens
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // Speichere Access-Token in der Datenbank
            saveToken(user, accessToken, TokenType.BEARER, jwtService.extractExpirationAsLocalDateTime(accessToken));

            // Bei erfolgreicher Anmeldung
            loginAttemptService.loginSucceeded(request.getEmail());

            // Erstelle und gib Antwort zurück
            return buildAuthResponse(user, accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            // Bei fehlgeschlagener Anmeldung
            loginAttemptService.loginFailed(request.getEmail());
            throw e;
        }
    }

    /**
     * Erneuert ein Access-Token mit einem Refresh-Token.
     *
     * @param request Die Token-Erneuerungsanfrage
     * @return Eine Authentifizierungsantwort mit neuem Access-Token
     * @throws InvalidTokenException wenn das Refresh-Token ungültig ist
     */
    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Extrahiere E-Mail aus Token
        String userEmail;
        try {
            userEmail = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        if (userEmail == null) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Lade Benutzer aus der Datenbank
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        // Überprüfe, ob der Benutzer aktiv ist
        if (!user.isActive()) {
            throw new AccountDisabledException("Account is disabled");
        }

        // Lade Benutzerdetails für Token-Validierung
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // Überprüfe Refresh-Token
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Generiere neues Access-Token
        String accessToken = jwtService.generateToken(userDetails);

        // Widerrufe alte Access-Tokens
        revokeAllUserTokens(user);

        // Speichere neues Access-Token
        saveToken(user, accessToken, TokenType.BEARER, jwtService.extractExpirationAsLocalDateTime(accessToken));

        // Erstelle und gib Antwort zurück
        return buildAuthResponse(user, accessToken, refreshToken);
    }

    /**
     * Meldet einen Benutzer ab und widerruft sein Token.
     *
     * @param authHeader Der Authorization-Header mit dem Token
     */
    @Transactional
    public void logout(String authHeader) {
        // Überprüfe, ob der Header gültig ist
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        // Extrahiere Token
        String jwt = authHeader.substring(7);

        // Finde Token in der Datenbank
        tokenRepository.findByToken(jwt).ifPresent(token -> {
            // Widerrufe Token
            token.setRevoked(true);
            token.setExpired(true);
            tokenRepository.save(token);
            log.info("Token revoked: {}", token.getId());
        });
    }

    /**
     * Speichert ein Token in der Datenbank.
     *
     * @param user Der Benutzer, dem das Token gehört
     * @param jwtToken Das JWT
     * @param tokenType Der Token-Typ
     * @param expiresAt Der Zeitpunkt, an dem das Token abläuft
     */
    private void saveToken(User user, String jwtToken, TokenType tokenType, LocalDateTime expiresAt) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(token);
        log.debug("Token saved: {}", token.getId());
    }

    /**
     * Widerruft alle aktiven Tokens eines Benutzers.
     *
     * @param user Der Benutzer, dessen Tokens widerrufen werden sollen
     */
    private void revokeAllUserTokens(User user) {
        if(user == null || user.getId() == null) {
            log.warn("Tried to revoke a Token for a null user");
            return;
        }

        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(
                user.getId(),
                LocalDateTime.now()
        );

        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
        log.debug("Revoked {} tokens for user: {}", validUserTokens.size(), user.getEmail());
    }

    /**
     * Erstellt eine Authentifizierungsantwort basierend auf dem Benutzer und den Tokens.
     *
     * @param user Der authentifizierte Benutzer
     * @param accessToken Das Access-Token
     * @param refreshToken Das Refresh-Token
     * @return Eine Authentifizierungsantwort
     */
    private AuthenticationResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        AuthenticationResponse.AuthenticationResponseBuilder builder = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .expiresAt(extractExpiryDate(accessToken));

        // Bestimme Benutzertyp und füge spezifische Informationen hinzu
        if (user instanceof Attendee) {
            builder.userType("ATTENDEE");
        } else if (user instanceof Organizer) {
            builder.userType("ORGANIZER");
            builder.organizationName(((Organizer) user).getOrganizationName());
        } else {
            builder.userType("USER");
        }

        return builder.build();
    }

    /**
     * Extrahiert das Ablaufdatum eines Tokens als Unix-Timestamp.
     *
     * @param token Das JWT
     * @return Der Zeitpunkt, an dem das Token abläuft, als Unix-Timestamp in Millisekunden
     */
    private Long extractExpiryDate(String token) {
        Date expiryDate = jwtService.extractClaim(token, Claims::getExpiration);
        return expiryDate != null ? expiryDate.getTime() : null;
    }

    /**
     * Validiert die Felder für die Attendee-Registrierung.
     *
     * @param request Die Registrierungsanfrage
     * @throws IllegalArgumentException wenn erforderliche Felder fehlen
     */
    private void validateAttendeeFields(RegistrationRequest request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required for attendees");
        }

        if (request.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required for attendees");
        }
    }

    /**
     * Validiert die Felder für die Organizer-Registrierung.
     *
     * @param request Die Registrierungsanfrage
     * @throws IllegalArgumentException wenn erforderliche Felder fehlen
     */
    private void validateOrganizerFields(RegistrationRequest request) {
        if (request.getOrganizationName() == null || request.getOrganizationName().isBlank()) {
            throw new IllegalArgumentException("Organization name is required for organizers");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required for organizers");
        }
    }
}