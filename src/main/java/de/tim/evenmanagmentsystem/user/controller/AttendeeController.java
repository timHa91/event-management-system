package de.tim.evenmanagmentsystem.user.controller;

import de.tim.evenmanagmentsystem.ticket.dto.TicketResponse;
import de.tim.evenmanagmentsystem.ticket.dto.TicketRegistrationDTO;
import de.tim.evenmanagmentsystem.user.dto.*;
import de.tim.evenmanagmentsystem.user.service.AttendeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendees")
@RequiredArgsConstructor
public class AttendeeController {

    private final AttendeeService attendeeService;

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentAttendee(@AuthenticationPrincipal UserDetails userDetails) {
        // Für Testzwecke geben wir null zurück
        return ResponseEntity.ok(null);
    }

    @PutMapping("/me")
    public ResponseEntity<AttendeeDTO> updateAttendee(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AttendeeUpdateDTO updateDTO) {
        return null;
    }

    @GetMapping("/me/tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(@AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @GetMapping("/me/registrations")
    public ResponseEntity<List<TicketRegistrationDTO>> getMyRegistrations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }
}
