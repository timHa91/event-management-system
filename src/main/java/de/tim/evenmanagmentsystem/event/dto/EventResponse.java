package de.tim.evenmanagmentsystem.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private Long version;
    private String uuid;
    private String title;
    private String description;
    private LocalDateTime startingAt;
    private LocalDateTime endingAt;
    private Long venueId;
    private String venueName;
    private String venueAddress;
    private int venueCapacity;
    private int capacity;
    private Set<String> categories;
    private Long organizerId;
    private String organizerName;
    private String imageUrl;

    private int availableTickets;
}