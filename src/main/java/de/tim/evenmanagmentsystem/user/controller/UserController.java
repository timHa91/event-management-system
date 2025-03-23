package de.tim.evenmanagmentsystem.user.controller;

import de.tim.evenmanagmentsystem.user.dto.PasswordChangeRequest;
import de.tim.evenmanagmentsystem.user.dto.UserDTO;
import de.tim.evenmanagmentsystem.user.mapper.UserMapper;
import de.tim.evenmanagmentsystem.user.model.User;
import de.tim.evenmanagmentsystem.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User currentUser = userService.findByEmail(email);

        return ResponseEntity.ok(userMapper.toDTO(currentUser));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request) {
        return null;
    }

    @GetMapping("/me/roles")
    public ResponseEntity<Set<String>> getUserRoles(@AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }
}
