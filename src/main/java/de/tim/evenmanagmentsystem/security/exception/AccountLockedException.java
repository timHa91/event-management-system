package de.tim.evenmanagmentsystem.security.exception;

/**
 * Exception für den Fall, dass das Password zu oft falsch eingegeben wurde.
 */
public class AccountLockedException extends RuntimeException {

    /**
     * Erstellt eine neue AccountLockedException mit der angegebenen Nachricht.
     *
     * @param message Die Fehlermeldung
     */
    public AccountLockedException(String message) {
        super(message);
    }
}
