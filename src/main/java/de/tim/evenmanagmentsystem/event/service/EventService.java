package de.tim.evenmanagmentsystem.event.service;

import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.dto.EventRequest;

import java.util.List;

public interface EventService {

    EventResponse createEvent(EventRequest request, Long organizerId);
    EventResponse updateEvent(EventRequest request, Long eventId, Long organizerId);
    EventResponse deleteEvent(Long eventId, Long organizerId);
    EventResponse getEventById(Long eventId, Long organizerId);
    List<EventResponse> getEventsByOrganizer(Long organizerId);
}
