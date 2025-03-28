package de.tim.evenmanagmentsystem.security.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tim.evenmanagmentsystem.security.config.MockJwtServiceConfig;
import de.tim.evenmanagmentsystem.security.config.TestSecurityConfig;
import de.tim.evenmanagmentsystem.security.controller.AuthenticationController;
import de.tim.evenmanagmentsystem.security.dto.AuthenticationResponse;
import de.tim.evenmanagmentsystem.security.dto.RegistrationRequest;
import de.tim.evenmanagmentsystem.security.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import({TestSecurityConfig.class, MockJwtServiceConfig.class})
@ActiveProfiles({"test", "test-data-init"})
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authService;

    @Test
    void shouldRegisterAttendee() throws Exception {
        // Given
        RegistrationRequest request = createAttendeeRegistrationRequest();
        AuthenticationResponse response = createAuthResponse();

        when(authService.registerAttendee(any(RegistrationRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/register/attendee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test.access.token"))
                .andExpect(jsonPath("$.refreshToken").value("test.refresh.token"))
                .andExpect(jsonPath("$.userType").value("ATTENDEE"));

        verify(authService).registerAttendee(any(RegistrationRequest.class));
    }

    // Weitere Tests...

    private RegistrationRequest createAttendeeRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("attendee@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return request;
    }

    private RegistrationRequest createOrganizerRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("organizer@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setOrganizationName("Test Organization");
        request.setDescription("Test Description");
        return request;
    }

    private AuthenticationResponse createAuthResponse() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken("test.access.token");
        response.setRefreshToken("test.refresh.token");
        response.setUserType("ATTENDEE");
        response.setUserId(1L);
        response.setEmail("test@example.com");
        response.setFirstName("Test");
        response.setLastName("User");
        return response;
    }
}