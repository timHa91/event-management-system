package com.th.eventmanagmentsystem.usermanagement.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @NotBlank(message = "Die E-Mail-Adresse darf nicht leer sein.")
        @Email(message = "Bitte geben Sie eine gültige E-Mail-Adresse an.")
        String email,

        @NotBlank(message = "Das Passwort darf nicht leer sein.")
        @Size(min = 8, max = 255, message = "Das Passwort muss zwischen 8 und 255 Zeichen lang sein.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                message = "Das Passwort muss mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten."
        )
        String password

) {}