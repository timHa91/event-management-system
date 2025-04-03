package de.tim.evenmanagmentsystem.event.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.respository.EventRepository;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventApiIntegrationTest {
//    Fokus: Vollständiger Request-Flow von API bis Datenbank

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @BeforeEach
    void setUp() {
        // Setup test data
    }

//    @Test
//    void createEvent_shouldCreateEventInDatabase() throws Exception {
//        // Given
//        Organizer organizer = createAndSaveTestOrganizer();
//        EventRequest request = createValidEventRequest();
//
//        // When/Then
//        mockMvc.perform(post("/api/events")
//                        .with(user(organizer))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated());
//
//        // Verify event was saved
//        List<Event> events = eventRepository.findAll();
////        assertThat(events).hasSize(1);
//        assertThat(events.get(0).getTitle()).isEqualTo(request.getTitle());
//    }
//
//    private EventRequest createValidEventRequest() {
//    }
//
//    private Organizer createAndSaveTestOrganizer() {
//        return null;
//    }
}
