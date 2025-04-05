package de.tim.evenmanagmentsystem.event.integration;

import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.mapper.EventMapper;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.respository.EventRepository;
import de.tim.evenmanagmentsystem.event.service.EventServiceImpl;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.repository.AttendeeRepository;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import de.tim.evenmanagmentsystem.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({EventServiceImpl.class, EventMapper.class})
class EventServiceIntegrationTest {
    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Organizer testOrganizer;
    private Venue testVenue;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        // Erstellen Sie einen Testorganisator
        testOrganizer = new Organizer();
        testOrganizer.setFirstName("Test Organizer");
        testOrganizer.setLastName("Test Organizer");
        testOrganizer.setEmail("organizer@test.com");
        testOrganizer.setPassword("password");
        organizerRepository.save(testOrganizer);

        // Erstellen Sie einen Testveranstaltungsort
        testVenue = new Venue();
        testVenue.setName("Test Venue");
        testVenue.setAddress(new Address("street", "city", "state", "country"));
        testVenue.setCapacity(200);
        venueRepository.save(testVenue);
    }

    @Test
    void createEvent_shouldPersistEventToDatabase() {
        // Given
        EventRequest request = EventRequest.builder()
                .title("Test Event")
                .description("Test Description")
                .startingAt(LocalDateTime.now().plusDays(1))
                .endingAt(LocalDateTime.now().plusDays(1).plusHours(2))
                .venueId(testVenue.getId()) // Verwenden Sie die ID des tatsächlich erstellten Veranstaltungsorts
                .capacity(100)
                .categories(Set.of("CONCERT", "THEATRE"))
                .build();

        // When
        EventResponse response = eventService.createEvent(request, testOrganizer.getId());

        // Then
        Optional<Event> savedEvent = eventRepository.findById(response.getId());
        assertThat(savedEvent).isPresent();
        assertThat(savedEvent.get().getTitle()).isEqualTo(request.getTitle());
        assertThat(savedEvent.get().getDescription()).isEqualTo(request.getDescription());
        assertThat(savedEvent.get().getCapacity()).isEqualTo(request.getCapacity());
        assertThat(savedEvent.get().getOrganizer().getId()).isEqualTo(testOrganizer.getId());
        assertThat(savedEvent.get().getVenue().getId()).isEqualTo(testVenue.getId());
    }

    @Test
    void getEventById_shouldReturnEvent_whenEventExists() {
        // Given
        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setStartingAt(LocalDateTime.now().plusDays(1));
        event.setEndingAt(LocalDateTime.now().plusDays(1).plusHours(2));
        event.setVenue(testVenue);
        event.setOrganizer(testOrganizer);
        event.setCapacity(100);
        event = eventRepository.save(event);

        // When
        EventResponse response = eventService.getEventById(event.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(event.getId());
        assertThat(response.getTitle()).isEqualTo(event.getTitle());
    }
}