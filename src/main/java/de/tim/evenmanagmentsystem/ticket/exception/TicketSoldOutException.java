package de.tim.evenmanagmentsystem.ticket.exception;

/**
 * Exception für den Fall, dass keine Tickets mehr vorhanden sind.
 */
public class TicketSoldOutException extends RuntimeException {
    /**
     * Erstellt eine neue TicketSoldOutException mit der angegebenen Nachricht.
     *
     * @param message Die Fehlermeldung
     */
    public TicketSoldOutException(String message) {
        super(message);
    }
    // ToDo: Excpetion in globalhandler einbringen
}
