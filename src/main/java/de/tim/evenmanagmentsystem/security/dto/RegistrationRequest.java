package de.tim.evenmanagmentsystem.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO für Registrierungsanfragen.
 * Enthält alle notwendigen Informationen für die Benutzerregistrierung.
 * Wird sowohl für Attendees als auch für Organizers verwendet.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null-Werte werden nicht serialisiert
public class RegistrationRequest {

    // Gemeinsame Felder für alle Benutzertypen

    /**
     * Die E-Mail-Adresse des Benutzers.
     * Muss ein gültiges E-Mail-Format haben und darf nicht leer sein.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Das Passwort des Benutzers.
     * Muss mindestens 8 Zeichen lang sein und darf nicht leer sein.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * Der Vorname des Benutzers.
     * Darf nicht leer sein.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * Der Nachname des Benutzers.
     * Darf nicht leer sein.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    // Attendee-spezifische Felder

    /**
     * Die Telefonnummer des Teilnehmers.
     */
    private String phoneNumber;

    /**
     * Das Geburtsdatum des Teilnehmers.
     * Muss in der Vergangenheit liegen.
     */
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    /**
     * Die Adresse des Teilnehmers.
     */
    private String address;

    /**
     * Die Stadt des Teilnehmers.
     */
    private String city;

    /**
     * Die Postleitzahl des Teilnehmers.
     */
    private String postalCode;

    /**
     * Das Land des Teilnehmers.
     */
    private String country;

    // Organizer-spezifische Felder

    /**
     * Der Name der Organisation.
     */
    private String organizationName;

    /**
     * Die Beschreibung der Organisation.
     */
    private String description;

    /**
     * Die Kontakt-E-Mail der Organisation.
     */
    @Email(message = "Contact email should be valid")
    private String contactEmail;

    /**
     * Die Kontakt-Telefonnummer der Organisation.
     */
    private String contactPhone;

    /**
     * Die Website der Organisation.
     */
    private String website;

    /**
     * Die Firmennummer der Organisation.
     */
    private String companyRegistrationNumber;
}