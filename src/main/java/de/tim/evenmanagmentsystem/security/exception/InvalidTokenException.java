package de.tim.evenmanagmentsystem.security.exception;

/**
 * Exception für den Fall, dass ein Token ungültig ist.
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * Erstellt eine neue InvalidTokenException mit der angegebenen Nachricht.
     *
     * @param message Die Fehlermeldung
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}