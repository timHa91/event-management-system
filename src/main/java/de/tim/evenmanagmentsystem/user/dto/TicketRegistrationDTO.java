package de.tim.evenmanagmentsystem.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketRegistrationDTO {
  @NotBlank(message = "Ticket type is required")
  private String ticketType;

  @NotNull(message = "Ticket price is required")
  private double ticketPrice;

  @NotNull(message = "Ticket status is required")
  private String ticketStatus;

  @NotNull(message = "Ticket ID is required")
  private String ticketId;

  @NotNull(message = "Event ID is required")
  private String eventId;

  @NotNull(message = "Attendee ID is required")
  private String attendeeId;
}
