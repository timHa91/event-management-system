package de.tim.evenmanagmentsystem.security.exception;

/**
 * Exception für den Fall, dass eine E-Mail-Adresse bereits verwendet wird.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Erstellt eine neue EmailAlreadyExistsException mit der angegebenen Nachricht.
     *
     * @param message Die Fehlermeldung
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}