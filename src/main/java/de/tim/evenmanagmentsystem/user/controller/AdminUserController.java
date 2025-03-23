package de.tim.evenmanagmentsystem.user.controller;

import de.tim.evenmanagmentsystem.user.dto.UserDTO;
import de.tim.evenmanagmentsystem.user.dto.UserStatusUpdateDTO;
import de.tim.evenmanagmentsystem.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        // Für Testzwecke geben wir eine leere Seite zurück
        return ResponseEntity.ok(new PageImpl<>(Collections.emptyList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        // Für Testzwecke geben wir null zurück
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusUpdateDTO statusDTO) {
        // Für Testzwecke geben wir null zurück
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Für Testzwecke geben wir noContent zurück
        return ResponseEntity.noContent().build();
    }
}