package de.tim.evenmanagmentsystem.event.controller;

import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.dto.EventUpdateRequest;
import de.tim.evenmanagmentsystem.event.service.EventServiceImpl;
import de.tim.evenmanagmentsystem.user.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "API zum Verwalten von Events")
@Slf4j
public class EventController {

    private final EventServiceImpl eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal User userDetails
    ) {
        Long userID = Objects.requireNonNull(userDetails.getId(), "User id cannot be null");
        EventResponse response = eventService.createEvent(request, userID);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable String uuid,
            @RequestBody EventUpdateRequest request,
            @AuthenticationPrincipal User userDetails
    ) {
        Long userId = Objects.requireNonNull(userDetails.getId(), "User id cannot be null");
        EventResponse response = eventService.updateEvent(request, uuid, userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<EventResponse> deleteEvent(
            @PathVariable String uuid,
            @AuthenticationPrincipal User userDetails
    ) {
        Long userId = Objects.requireNonNull(userDetails.getId(), "User id cannot be null");
        eventService.deleteEvent(uuid, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startingAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<EventResponse> events = eventService.findAll(pageable);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}/details")
    public ResponseEntity<Object> getEventDetails(@PathVariable Long eventId) {
        EventResponse event = eventService.getEventById(eventId);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/organizer")
    public ResponseEntity<Page<EventResponse>> getAllEventsByOrganizer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startingAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal User userDetails
    ) {
        Long userId = Objects.requireNonNull(userDetails.getId(), "User id cannot be null");

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<EventResponse> event = eventService.getEventsByOrganizer(userId, pageable);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/{city}")
    public ResponseEntity<Page<EventResponse>> getAllEventsByCity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startingAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @PathVariable String city
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<EventResponse> events = eventService.getEventsByCity(city, pageable);

        return ResponseEntity.ok(events);
    }
}