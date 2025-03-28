package de.tim.evenmanagmentsystem.event.mapper;

import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.model.EventCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EventMapper {

    public EventResponse toResponse(final Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startingAt(event.getStartingAt())
                .endingAt(event.getEndingAt())
                .venueId(event.getVenue().getId())
                .venueName(event.getVenue().getName())
                .venueAddress(event.getVenue().getAddress().getFormatedAddress())
                .venueCapacity(event.getVenue().getCapacity())
                .capacity(event.getCapacity())
                .categories(mapCategoriesToString(event.getCategories()))
                .organizerId(event.getOrganizer().getId())
                .organizerName(event.getOrganizer().getOrganizationName())
                .imageUrl(event.getImageUrl())
                .build();
    }

    public Set<String> mapCategoriesToString(final Set<EventCategory> categories) {
        return categories.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    public Set<EventCategory> mapStringsToCategories(final Set<String> categories) {
        return categories.stream()
                .map(category -> {
                    try {
                        return EventCategory.valueOf(category);
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid event category: {}", category);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}