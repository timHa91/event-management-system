package de.tim.evenmanagmentsystem.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
  private String ticketId;
  private String eventId;
  private String attendeeId;
  private String ticketType;
  private String ticketStatus;
  private String ticketPrice;
}
