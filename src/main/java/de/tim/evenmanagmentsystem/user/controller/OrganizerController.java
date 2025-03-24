package de.tim.evenmanagmentsystem.user.controller;

import de.tim.evenmanagmentsystem.event.dto.EventDTO;
import de.tim.evenmanagmentsystem.user.dto.OrganizerDTO;
import de.tim.evenmanagmentsystem.user.dto.OrganizerStatisticsDTO;
import de.tim.evenmanagmentsystem.user.dto.OrganizerUpdateDTO;
import de.tim.evenmanagmentsystem.user.service.OrganizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/organizers")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;

    @GetMapping("/me")
    public ResponseEntity<OrganizerDTO> getCurrentOrganizer(@AuthenticationPrincipal UserDetails userDetails) {
        // Für Testzwecke geben wir null zurück
        return ResponseEntity.ok(null);
    }

    @PutMapping("/me")
    public ResponseEntity<OrganizerDTO> updateOrganizer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrganizerUpdateDTO updateDTO) {
        // Für Testzwecke geben wir null zurück
        return ResponseEntity.ok(null);
    }

    @GetMapping("/me/events")
    public ResponseEntity<Page<EventDTO>> getMyEvents(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        // Für Testzwecke geben wir eine leere Seite zurück
        return ResponseEntity.ok(new PageImpl<>(Collections.emptyList()));
    }

    @GetMapping("/me/statistics")
    public ResponseEntity<OrganizerStatisticsDTO> getMyStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        // Für Testzwecke geben wir null zurück
        return ResponseEntity.ok(null);
    }
}
