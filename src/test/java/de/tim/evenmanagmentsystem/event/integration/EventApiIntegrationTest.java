package de.tim.evenmanagmentsystem.event.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tim.evenmanagmentsystem.event.respository.EventRepository;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
