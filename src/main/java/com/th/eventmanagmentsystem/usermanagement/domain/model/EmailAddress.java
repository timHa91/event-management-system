package com.th.eventmanagmentsystem.usermanagement.domain.model;

import java.util.Objects;

public record EmailAddress (
    String email
) {

    public EmailAddress {
        Objects.requireNonNull(email, "Email darf nicht null sein");
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email darf nicht leer sein");
        }
    }

    public static EmailAddress of(String email) {
        String trimmedEmail = (email == null) ? null : email.trim();
        return new EmailAddress(trimmedEmail);
    }
}
