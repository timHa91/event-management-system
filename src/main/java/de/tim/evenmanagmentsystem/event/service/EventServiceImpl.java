package de.tim.evenmanagmentsystem.event.service;

import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.mapper.EventMapper;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.security.exception.NotFoundException;
import de.tim.evenmanagmentsystem.security.exception.UserNotFoundException;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import de.tim.evenmanagmentsystem.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final VenueRepository venueRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request, Long organizerId) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(UserNotFoundException::new);

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new NotFoundException("Venue"));

        Event event = new Event(
                request.getTitle(),
                request.getStartingAt(),
                request.getEndingAt(),
                venue,
                request.getCapacity(),
                organizer
        );
        event.setDescription(request.getDescription());
        event.setCategories(eventMapper.mapStringsToCategories(request.getCategories()));

        eventRepository.save(event);

        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(EventRequest request, Long eventId, Long organizerId) {
        return null;
    }

    @Override
    @Transactional
    public EventResponse deleteEvent(Long eventId, Long organizerId) {
        return null;
    }

    @Override
    public EventResponse getEventById(Long eventId, Long organizerId) {
        return null;
    }

    @Override
    public List<EventResponse> getEventsByOrganizer(Long organizerId) {
        return List.of();
    }
}
