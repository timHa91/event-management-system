package de.tim.evenmanagmentsystem.security.exception;

/**
 * Exception für Zugriffsverweigerungen auf Ressourcen.
 * Wird geworfen, wenn ein Benutzer nicht berechtigt ist,
 * eine bestimmte Ressource zu lesen oder zu ändern.
 */
public class ResourceAccessDeniedException extends RuntimeException {

    public ResourceAccessDeniedException(String message) {
        super(message);
    }

    public ResourceAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
