package de.tim.evenmanagmentsystem.event.dto;

import de.tim.evenmanagmentsystem.ticket.dto.TicketTypeRequest;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Start date and time is required")
    private LocalDateTime startingAt;

    @NotNull(message = "End date and time is required")
    private LocalDateTime endingAt;

    @NotNull(message = "Venue ID is required")
    private Long venueId;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    @Valid
    private Set<TicketTypeRequest> ticketTypes;

    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;

    private Set<String> categories;
}