package de.tim.evenmanagmentsystem.event.controller;

import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.service.EventService;
import de.tim.evenmanagmentsystem.user.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "API zum Verwalten von Events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<Object>> getAllPublicEvents() {
        // Für Testzwecke geben wir eine leere Liste zurück
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/{eventId}/details")
    public ResponseEntity<Object> getEventDetails(@PathVariable Long eventId) {
        // Für Testzwecke geben wir null zurück
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest eventRequest,
            @AuthenticationPrincipal User userDetails
    ) {
        Long userID = Objects.requireNonNull(userDetails.getId(), "User id cannot be null");

        EventResponse response = eventService.createEvent(eventRequest, userID);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}