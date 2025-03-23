package de.tim.evenmanagmentsystem.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Dieser Controller wurde entfernt, da er in Konflikt mit AdminUserController
 * steht.
 * Der AdminUserController bietet bereits die Endpunkte unter /api/admin/users
 * an.
 */
// @RestController
// @RequestMapping("/api/admin")
public class AdminController {

  // @GetMapping("/users")
  public ResponseEntity<List<Object>> getAllUsers() {
    // Für Testzwecke geben wir eine leere Liste zurück
    return ResponseEntity.ok(Collections.emptyList());
  }
}