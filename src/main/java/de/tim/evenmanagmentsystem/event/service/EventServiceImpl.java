package de.tim.evenmanagmentsystem.event.service;

import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.dto.EventUpdateRequest;
import de.tim.evenmanagmentsystem.event.mapper.EventMapper;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.respository.EventRepository;
import de.tim.evenmanagmentsystem.security.exception.*;
import de.tim.evenmanagmentsystem.ticket.model.TicketType;
import de.tim.evenmanagmentsystem.ticket.repository.TicketRepository;
import de.tim.evenmanagmentsystem.ticket.repository.TicketTypeRepository;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import de.tim.evenmanagmentsystem.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final VenueRepository venueRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request, Long organizerId) {
        log.info("Creating new event for organizer: {}", organizerId);

        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(UserNotFoundException::new);

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new NotFoundException("Venue"));

        try {
            Event event = new Event(
                    request.getTitle(),
                    request.getStartingAt(),
                    request.getEndingAt(),
                    venue,
                    request.getCapacity(),
                    organizer
            );

            if (request.getDescription() != null) {
                event.setDescription(request.getDescription());
            }

            if (request.getCategories() != null) {
                event.setCategories(eventMapper.mapStringsToCategories(request.getCategories()));
            }

            if (request.getImageUrl() != null) {
                event.setImageUrl(request.getImageUrl());
            }

            Event savedEvent = eventRepository.save(event);
            log.info("Event created successfully with ID: {}", savedEvent.getId());

            return eventMapper.toResponse(savedEvent);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create event: {}", e.getMessage());
            throw new InvalidRequestException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public EventResponse updateEvent(EventUpdateRequest request, String uuid, Long organizerId) {
        log.info("Event with UUID {} updated by organizer {}", uuid, organizerId);

        Event event = eventRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Event"));

        if (!Objects.equals(event.getVersion(), request.getVersion())) {
            log.warn("Version conflict detected for event: {}", uuid);
            throw new ConflictException("Event has been modified by another user. Please refresh and try again.");
        }

        if (!event.getOrganizer().getId().equals(organizerId)) {
            log.warn("Unauthorized update attempt for event: {} by organizer: {}", uuid, organizerId);
            throw new ResourceAccessDeniedException("You are not authorized to update this event");
        }

        if (request.getCapacity() != null) {
            int soldTickets = ticketTypeRepository.findByEventId(event.getId());
            if (request.getCapacity() < soldTickets) {
                log.warn("Cannot reduce capacity below sold tickets. Capacity: {}, Sold: {}",
                        request.getCapacity(), soldTickets);
                throw new InvalidRequestException(
                        String.format("Cannot reduce capacity to %d as %d tickets are already sold",
                                request.getCapacity(), soldTickets));
            }

            event.setCapacity(request.getCapacity());
        }

        try {
            if (request.getVenueId() != null) {
                Venue venue = venueRepository.findById(request.getVenueId())
                        .orElseThrow(() -> new NotFoundException("Venue"));

                int effectiveCapacity = request.getCapacity() != null ? request.getCapacity() : event.getCapacity();
                if (effectiveCapacity > venue.getCapacity()) {
                    throw new InvalidRequestException(
                            String.format("Event capacity (%d) cannot exceed venue capacity (%d)",
                                    effectiveCapacity, venue.getCapacity()));
                }

                event.setVenue(venue);
            }

            if (request.getTitle() != null) {
                event.setTitle(request.getTitle());
            }

            if (request.getDescription() != null) {
                event.setDescription(request.getDescription());
            }

            LocalDateTime newStart = request.getStartingAt();
            LocalDateTime newEnd = request.getEndingAt();

            if (newStart != null && newEnd != null && newStart.isAfter(newEnd)) {
                throw new InvalidRequestException("Start time must be before end time");
            }

            if (newStart != null) {
                event.setStartingAt(newStart);
            }

            if (newEnd != null) {
                event.setEndingAt(newEnd);
            }

            if (request.getCapacity() != null) {
                event.setCapacity(request.getCapacity());
            }

            if (request.getCategories() != null) {
                event.setCategories(eventMapper.mapStringsToCategories(request.getCategories()));
            }

            if (request.getImageUrl() != null) {
                event.setImageUrl(request.getImageUrl());
            }

            Event updatedEvent = eventRepository.save(event);
            log.info("Event updated successfully: {}", uuid);

            return eventMapper.toResponse(updatedEvent);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update event: {}", e.getMessage());
            throw new InvalidRequestException(e.getMessage());
        }
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
