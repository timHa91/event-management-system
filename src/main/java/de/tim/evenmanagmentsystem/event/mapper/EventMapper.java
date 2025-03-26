package de.tim.evenmanagmentsystem.event.mapper;

import com.sun.jdi.request.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.model.EventCategory;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEntity(EventRequest request);
    EventResponse toResponse(Event event);

    default Venue mapVenueIdToVenue(Long venueId) {
        if (venueId == null) {
            return null;
        }
        Venue venue = new Venue();
        venue.setId(venueId);
        return venue;
    }

    default Set<EventCategory> mapStringsToCategories(Set<String> categories) {
        if (categories == null) {
            return new HashSet<>();
        }

        return categories.stream()
                .map(str -> {
                    try {
                        return EventCategory.valueOf(str);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}