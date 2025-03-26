package de.tim.evenmanagmentsystem.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.service.EventRepository;
import de.tim.evenmanagmentsystem.event.service.EventService;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizerRepository organizerRepository;

    @MockBean
    private EventService eventService;

    private Attendee testAttendee;
    private Organizer testOrganizer;
    private EventRequest validRequest;
    private EventResponse mockResponse;
    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        testAttendee = new Attendee();
        testAttendee.setEmail("attendee@test.com");
        testAttendee.setFirstName("Tim");
        testAttendee.setLastName("Evenmanagmentsystem");
        testAttendee.setPassword(passwordEncoder.encode("password"));
        testAttendee.setDateOfBirth(LocalDate.of(1991,4,10));
        testAttendee.setPhoneNumber("+4123123123");
        testAttendee.setRoles(Set.of(UserRole.ROLE_ATTENDEE));

        testOrganizer = new Organizer();
        testOrganizer.setEmail("organizer@test.com");
        testOrganizer.setFirstName("Tim");
        testOrganizer.setLastName("Evenmanagmentsystem");
        testOrganizer.setPassword(passwordEncoder.encode("password"));
        testOrganizer.setDescription("Test organizer");
        testOrganizer.setBankAccountInfo("1234567890");
        testOrganizer.setOrganizationName("Test Organization");
        testOrganizer.setCompanyRegistrationNumber("1234567890");
        testOrganizer.setRoles(Set.of(UserRole.ROLE_ORGANIZER));

        organizerRepository.save(testOrganizer);

        validRequest = new EventRequest();
        validRequest.setTitle("Test Event");
        validRequest.setDescription("Test Description");
        validRequest.setStartingAt(LocalDateTime.now().plusDays(1));
        validRequest.setEndingAt(LocalDateTime.now().plusDays(1).plusHours(2));
        validRequest.setVenueId(1L);
        validRequest.setCapacity(100);
        validRequest.setCategories(Set.of("CONCERT", "THEATRE"));

        mockResponse = new EventResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle(validRequest.getTitle());
        mockResponse.setDescription(validRequest.getDescription());
        mockResponse.setStartDate(validRequest.getStartingAt());
        mockResponse.setEndDate(validRequest.getEndingAt());
    }

    @Test
    void createEvent_asOrganizer_shouldCreateEvent() throws Exception {
        // Mock den Service, um ein Ergebnis zurückzugeben
        when(eventService.createEvent(any(EventRequest.class), anyLong()))
                .thenReturn(mockResponse);

        // Führe die Request aus
        mockMvc.perform(post("/api/events")
                        .with(user(testOrganizer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockResponse.getId()))
                .andExpect(jsonPath("$.title").value(mockResponse.getTitle()));

        // Verifiziere, dass der Service aufgerufen wurde
        verify(eventService).createEvent(any(EventRequest.class), anyLong());
    }

    @Test
    void create_asAttendee_shouldNotCreateEvent_AndThrowException() throws Exception {
        mockMvc.perform(post("/api/events")
                .with(user(testAttendee))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());

        verify(eventService, never()).createEvent(any(EventRequest.class), anyLong());
    }

    @Test
    void createEvent_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }
}