package de.tim.evenmanagmentsystem.user.controller;

import de.tim.evenmanagmentsystem.user.dto.EventDTO;
import de.tim.evenmanagmentsystem.user.dto.OrganizerDTO;
import de.tim.evenmanagmentsystem.user.dto.OrganizerStatisticsDTO;
import de.tim.evenmanagmentsystem.user.dto.OrganizerUpdateDTO;
import de.tim.evenmanagmentsystem.user.service.OrganizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizers")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;

    @GetMapping("/me")
    public ResponseEntity<OrganizerDTO> getCurrentOrganizer(@AuthenticationPrincipal UserDetails userDetails) {
        // Aktuellen Veranstalter abrufen
        return null;
    }

    @PutMapping("/me")
    public ResponseEntity<OrganizerDTO> updateOrganizer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrganizerUpdateDTO updateDTO) {
        // Veranstalterdaten aktualisieren
        return null;
    }

    @GetMapping("/me/events")
    public ResponseEntity<Page<EventDTO>> getMyEvents(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        // Events des aktuellen Veranstalters abrufen
        return null;
    }

    @GetMapping("/me/statistics")
    public ResponseEntity<OrganizerStatisticsDTO> getMyStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        // Statistiken des aktuellen Veranstalters abrufen
        return null;
    }
}
