package de.tim.evenmanagmentsystem.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.dto.EventUpdateRequest;
import de.tim.evenmanagmentsystem.event.service.EventServiceImpl;
import de.tim.evenmanagmentsystem.security.exception.NotFoundException;
import de.tim.evenmanagmentsystem.security.exception.ResourceAccessDeniedException;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EventServiceImpl eventService;

    private Attendee testAttendee;
    private Organizer testOrganizer;
    private EventUpdateRequest validUpdateRequest;
    private EventRequest validCreateRequest;
    private EventResponse mockCreateResponse;
    private EventResponse mockUpdateResponse;
    private String eventUuid = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        // Setup test attendee
        testAttendee = new Attendee();
        testAttendee.setId(1L);
        testAttendee.setEmail("attendee@test.com");
        testAttendee.setFirstName("Tim");
        testAttendee.setLastName("Test");
        testAttendee.setPassword("encoded_password");
        testAttendee.setDateOfBirth(LocalDate.of(1991, 4, 10));
        testAttendee.setPhoneNumber("+4123123123");
        testAttendee.setRoles(Set.of(UserRole.ROLE_ATTENDEE));

        // Setup test organizer
        testOrganizer = new Organizer();
        testOrganizer.setId(2L);
        testOrganizer.setEmail("organizer@test.com");
        testOrganizer.setFirstName("Tim");
        testOrganizer.setLastName("Organizer");
        testOrganizer.setPassword("encoded_password");
        testOrganizer.setDescription("Test organizer");
        testOrganizer.setBankAccountInfo("1234567890");
        testOrganizer.setOrganizationName("Test Organization");
        testOrganizer.setCompanyRegistrationNumber("1234567890");
        testOrganizer.setRoles(Set.of(UserRole.ROLE_ORGANIZER));

        // Setup valid create request
        validCreateRequest = EventRequest.builder()
                .title("Test Event")
                .description("Test Description")
                .startingAt(LocalDateTime.now().plusDays(1))
                .endingAt(LocalDateTime.now().plusDays(1).plusHours(2))
                .venueId(1L)
                .capacity(100)
                .categories(Set.of("CONCERT", "THEATRE"))
                .build();

        // Setup valid update request
        validUpdateRequest = EventUpdateRequest.builder()
                .title("Updated Event Title")
                .description("Updated description")
                .build();

        // Setup mock create response
        mockCreateResponse = EventResponse.builder()
                .id(1L)
                .version(1L)
                .title(validUpdateRequest.getTitle())
                .description(validUpdateRequest.getDescription())
                .startingAt(validCreateRequest.getStartingAt())
                .endingAt(validCreateRequest.getEndingAt())
                .build();

        // Setup mock update response
        mockUpdateResponse = EventResponse.builder()
                .id(1L)
                .uuid(eventUuid)
                .title(validUpdateRequest.getTitle())
                .description(validUpdateRequest.getDescription())
                .startingAt(validUpdateRequest.getStartingAt())
                .endingAt(validUpdateRequest.getEndingAt())
                .build();

        // Mock password encoder behavior if needed
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
    }

    @Test
    @DisplayName("Organizer should be able to create an event")
    void createEvent_asOrganizer_shouldCreateEvent() throws Exception {
        // Given
        when(eventService.createEvent(any(EventRequest.class), anyLong()))
                .thenReturn(mockCreateResponse);

        // When & Then
        mockMvc.perform(post("/api/events")
                        .with(user(testOrganizer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockCreateResponse.getId()))
                .andExpect(jsonPath("$.title").value(mockCreateResponse.getTitle()));

        verify(eventService).createEvent(any(EventRequest.class), anyLong());
    }

    @Test
    @DisplayName("Attendee should not be able to create an event")
    void createEvent_asAttendee_shouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/events")
                        .with(user(testAttendee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isForbidden());

        verify(eventService, never()).createEvent(any(EventRequest.class), anyLong());
    }

    @Test
    @DisplayName("Unauthenticated request should return unauthorized")
    void createEvent_unauthenticated_shouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isUnauthorized());

        verify(eventService, never()).createEvent(any(), anyLong());
    }

    @Test
    @DisplayName("Organizer should be able to update an event")
    void updateEvent_asOrganizer_shouldUpdateEvent() throws Exception {
        // Given
        when(eventService.updateEvent(any(EventUpdateRequest.class), eq(eventUuid), eq(testOrganizer.getId())))
                .thenReturn(mockUpdateResponse);

        // When & Then
        mockMvc.perform(patch("/api/events/{uuid}", eventUuid)
                        .with(user(testOrganizer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(eventUuid))
                .andExpect(jsonPath("$.title").value(validUpdateRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(validUpdateRequest.getDescription()));

        verify(eventService).updateEvent(any(EventUpdateRequest.class), eq(eventUuid), eq(testOrganizer.getId()));
    }

    @Test
    @DisplayName("Attendee should not be able to update an event")
    void updateEvent_asAttendee_shouldReturnForbidden() throws Exception {
        // Given
        when(eventService.updateEvent(any(EventUpdateRequest.class), eq(eventUuid), eq(testOrganizer.getId())))
                .thenReturn(mockUpdateResponse);

        // When & Then
        mockMvc.perform(patch("/api/events/{uuid}", eventUuid)
                        .with(user(testAttendee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isForbidden());

        verify(eventService, never()).updateEvent(any(EventUpdateRequest.class), eq(eventUuid), eq(testOrganizer.getId()));
    }

    @Test
    @DisplayName("Should return not found when event UUID doesn't exist")
    void updateEvent_withNonExistentUuid_shouldReturnNotFound() throws Exception {
        String falseUuid = "error_uuid";
        // Given
        when(eventService.updateEvent(any(EventUpdateRequest.class), eq(falseUuid), eq(testOrganizer.getId())))
                .thenThrow(new NotFoundException("Event not found"));

        // When & Then
        mockMvc.perform(patch("/api/events/{uuid}", falseUuid)
                .with(user(testOrganizer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isNotFound());

        verify(eventService, never()).updateEvent(any(EventUpdateRequest.class), eq(eventUuid), eq(testOrganizer.getId()));
    }

    @Test
    @DisplayName("Should return forbidden when organizer tries to update another organizer's event")
    void updateEvent_asOrganizerOfAnotherEvent_shouldReturnForbidden() throws Exception {
        Organizer falseOrganizer = new Organizer();
        falseOrganizer.setId(3L);
        falseOrganizer.setEmail("unauthorized@test.com");
        falseOrganizer.setFirstName("Tom");
        falseOrganizer.setLastName("Organizer");
        falseOrganizer.setPassword("encoded_password");
        falseOrganizer.setDescription("Test organizer");
        falseOrganizer.setBankAccountInfo("1234567890");
        falseOrganizer.setOrganizationName("Test Organization");
        falseOrganizer.setCompanyRegistrationNumber("1234567890");
        falseOrganizer.setRoles(Set.of(UserRole.ROLE_ORGANIZER));

        // Given
        when(eventService.updateEvent(any(EventUpdateRequest.class), eq(eventUuid), eq(falseOrganizer.getId())))
                .thenThrow(new ResourceAccessDeniedException("You are not authorized to update this event"));

        // When & Then
        mockMvc.perform(patch("/api/events/{uuid}", eventUuid)
                        .with(user(falseOrganizer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isForbidden());

        verify(eventService).updateEvent(any(EventUpdateRequest.class), eq(eventUuid), eq(falseOrganizer.getId()));
    }
}