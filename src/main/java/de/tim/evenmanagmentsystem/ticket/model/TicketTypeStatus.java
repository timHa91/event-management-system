package de.tim.evenmanagmentsystem.ticket.model;

/**
 * Status eines Ticket-Typs
 */
public enum TicketTypeStatus {
    ON_SALE,        // Verkauf aktiv
    SOLD_OUT,       // Ausverkauft
    NOT_YET_ON_SALE, // Verkauf noch nicht gestartet
    SALE_ENDED,     // Verkaufszeit abgelaufen
    CANCELLED       // Verkauf abgebrochen/storniert
}
