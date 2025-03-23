package de.tim.evenmanagmentsystem.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception für den Fall, dass ein Benutzerkonto deaktiviert ist.
 * Erbt von AuthenticationException, um von Spring Security korrekt behandelt zu werden.
 */
public class AccountDisabledException extends AuthenticationException {

    /**
     * Erstellt eine neue AccountDisabledException mit der angegebenen Nachricht.
     *
     * @param message Die Fehlermeldung
     */
    public AccountDisabledException(String message) {
        super(message);
    }
}