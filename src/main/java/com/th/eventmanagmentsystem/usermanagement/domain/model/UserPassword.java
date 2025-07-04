package com.th.eventmanagmentsystem.usermanagement.domain.model;

import java.util.Objects;

public record UserPassword(
        String hashedPassword
) {
    public UserPassword {
        Objects.requireNonNull(hashedPassword, "Password-Hash darf nicht null sein");
        if (hashedPassword.length() < 60 || hashedPassword.length() > 255) {
            throw new IllegalArgumentException("Ungültiger Passwort-Hash");
        }
    }

    public static UserPassword of (String rawHashedPassword) {
        String trimmedPassword = (rawHashedPassword == null) ? null : rawHashedPassword.trim();

        return new UserPassword(trimmedPassword);
    }
}