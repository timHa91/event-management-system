package de.tim.evenmanagmentsystem.event.service;

import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.mapper.EventMapper;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.model.EventCategory;
import de.tim.evenmanagmentsystem.security.exception.NotFoundException;
import de.tim.evenmanagmentsystem.security.exception.UserNotFoundException;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import de.tim.evenmanagmentsystem.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createEvent_withValidData_shouldCreateAndReturnEvent() {
        // Arrange
        Long organizerId = 1L;
        Organizer organizer = new Organizer();
        organizer.setId(organizerId);
        organizer.setOrganizationName("Test Org");

        Long venueId = 2L;
        Venue venue = new Venue();
        venue.setId(venueId);
        venue.setName("Test Venue");
        venue.setCapacity(200);

        var request = EventRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .startingAt(LocalDateTime.now().plusDays(1))
                .endingAt(LocalDateTime.now().plusDays(2))
                .venueId(venueId)
                .capacity(100)
                .categories(Set.of("CONCERT", "THEATER"))
                .build();

        Event savedEvent = new Event(
                request.getTitle(),
                request.getStartingAt(),
                request.getEndingAt(),
                venue,
                request.getCapacity(),
                organizer
        );
        savedEvent.setId(3L);
        savedEvent.setDescription(request.getDescription());
        savedEvent.setCategories(Set.of(EventCategory.THEATRE, EventCategory.CONCERT));


        EventResponse expectedResponse = new EventResponse();
        expectedResponse.setId(savedEvent.getId());
        expectedResponse.setTitle(savedEvent.getTitle());
        expectedResponse.setDescription(savedEvent.getDescription());
        expectedResponse.setCapacity(savedEvent.getCapacity());

        // Mock repository responses
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(eventMapper.mapStringsToCategories(request.getCategories()))
                .thenReturn(Set.of(EventCategory.THEATRE, EventCategory.CONCERT));
        when(eventMapper.toResponse(any(Event.class))).thenReturn(expectedResponse);

        // Act
        EventResponse response = eventService.createEvent(request, organizerId);

        // Assert
        assertNotNull(response);
        assertEquals(savedEvent.getId(), response.getId());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getCapacity(), response.getCapacity());

        // Verify
        verify(organizerRepository).findById(organizerId);
        verify(venueRepository).findById(venueId);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void createEvent_withNonExistingOrganizer_shouldThrowException() {
        // Arrange
        Long organizerId = 999L;
        Long venueId = 2L;
        var request = EventRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .startingAt(LocalDateTime.now().plusDays(1))
                .endingAt(LocalDateTime.now().plusDays(2))
                .venueId(venueId)
                .capacity(100)
                .categories(Set.of("CONCERT", "THEATER"))
                .build();

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            eventService.createEvent(request, organizerId);
        });

        verify(organizerRepository).findById(organizerId);
        verifyNoInteractions(eventRepository);
    }

    @Test
    void createEvent_withNonExistingVenue_shouldThrowException() {
        // Arrange
        Long organizerId = 1L;
        Organizer organizer = new Organizer();
        organizer.setId(organizerId);
        organizer.setOrganizationName("Test Org");

        Long venueId = 2L;
        var request = EventRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .startingAt(LocalDateTime.now().plusDays(1))
                .endingAt(LocalDateTime.now().plusDays(2))
                .venueId(venueId)
                .capacity(100)
                .categories(Set.of("CONCERT", "THEATER"))
                .build();

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            eventService.createEvent(request, organizerId);
        });

        verify(organizerRepository).findById(organizerId);
        verify(venueRepository).findById(venueId);
        verifyNoInteractions(eventRepository);
    }

    @Test
    void createEvent_withWrongStartingAt_shouldThrowException() {
        // Arrange
        Long organizerId = 1L;
        Organizer organizer = new Organizer();
        organizer.setId(organizerId);
        organizer.setOrganizationName("Test Org");

        Long venueId = 2L;
        Venue venue = new Venue();
        venue.setId(venueId);
        venue.setName("Test Venue");
        venue.setCapacity(200);

        var request = EventRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .startingAt(LocalDateTime.now().plusDays(1).plusHours(4))
                .endingAt(LocalDateTime.now().plusDays(1).plusHours(2))
                .venueId(venueId)
                .capacity(100)
                .categories(Set.of("CONCERT", "THEATER"))
                .build();

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(request, organizerId);
        });

        verify(organizerRepository).findById(organizerId);
        verify(venueRepository).findById(venueId);
        verifyNoInteractions(eventRepository);
    }
}
