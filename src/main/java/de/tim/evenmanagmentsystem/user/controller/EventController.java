package de.tim.evenmanagmentsystem.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

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
}