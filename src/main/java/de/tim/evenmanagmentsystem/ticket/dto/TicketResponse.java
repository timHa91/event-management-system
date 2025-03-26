package de.tim.evenmanagmentsystem.ticket.dto;

import lombok.Data;

@Data
public class TicketResponse {
    private String ticketId;
    private String eventId;
    private String attendeeId;
    private String ticketType;
    private String ticketStatus;
    private String ticketPrice;
}
