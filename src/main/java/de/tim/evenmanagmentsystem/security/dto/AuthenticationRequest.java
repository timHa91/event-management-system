package de.tim.evenmanagmentsystem.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO für Authentifizierungsanfragen (Login).
 * Enthält E-Mail und Passwort für die Benutzeranmeldung.
 */
@Data
public class AuthenticationRequest {

    /**
     * Die E-Mail-Adresse des Benutzers.
     * Muss ein gültiges E-Mail-Format haben und darf nicht leer sein.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Das Passwort des Benutzers.
     * Darf nicht leer sein.
     */
    @NotBlank(message = "Password is required")
    private String password;
}