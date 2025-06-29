package com.th.eventmanagmentsystem.usermanagement.api;

import com.th.eventmanagmentsystem.usermanagement.application.registration.RegisterUserUseCase;
import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegistrationController {

    private RegisterUserUseCase registrationService;

    @PostMapping()
    public ResponseEntity<UserRegistrationResponse> register(@Valid UserRegistrationRequest request) {
        UserRegistrationResponse response = registrationService.register(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{uuid}")
                .buildAndExpand(response.uuid())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
