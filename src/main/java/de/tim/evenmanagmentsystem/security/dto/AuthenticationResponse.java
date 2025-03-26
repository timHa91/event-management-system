package de.tim.evenmanagmentsystem.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO für Authentifizierungsantworten.
 * Enthält Tokens und grundlegende Benutzerinformationen.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null-Werte werden nicht serialisiert
public class AuthenticationResponse {

    /**
     * Das Access-Token für API-Zugriffe.
     */
    private String accessToken;

    /**
     * Das Refresh-Token für die Token-Erneuerung.
     */
    private String refreshToken;

    /**
     * Der Typ des Benutzers (ATTENDEE, ORGANIZER, ADMIN).
     */
    private String userType;

    /**
     * Die ID des Benutzers.
     */
    private Long userId;

    /**
     * Die E-Mail-Adresse des Benutzers.
     */
    private String email;

    /**
     * Der Vorname des Benutzers.
     */
    private String firstName;

    /**
     * Der Nachname des Benutzers.
     */
    private String lastName;

    /**
     * Der Name der Organisation (nur für Organizer).
     */
    private String organizationName;

    /**
     * Zeitpunkt, wann das Access-Token abläuft (in Millisekunden seit der Epoche).
     */
    private Long expiresAt;
}