package de.tim.evenmanagmentsystem.event.service;

import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {

    EventResponse createEvent(EventRequest request, Long organizerId);
    EventResponse updateEvent(EventUpdateRequest request, String uuid, Long organizerId);
    void deleteEvent(String uuid, Long organizerId);

    Page<EventResponse> findAll(Pageable pageable);
    EventResponse getEventById(Long eventId);
    Page<EventResponse> getEventsByOrganizer(Long organizerId, Pageable pageable);
    Page<EventResponse> getEventsByCity(String city, Pageable pageable);
}

