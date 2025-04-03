package de.tim.evenmanagmentsystem.event.dto;

import de.tim.evenmanagmentsystem.ticket.dto.TicketTypeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class EventUpdateRequest {
    private Long version;

    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private LocalDateTime startingAt;

    private LocalDateTime endingAt;

    private Long venueId;

    private Integer capacity;

    @Valid
    private Set<TicketTypeRequest> ticketTypes;

    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;

    private Set<String> categories;
}
