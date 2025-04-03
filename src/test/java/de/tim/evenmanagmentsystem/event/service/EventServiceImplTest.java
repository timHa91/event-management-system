package de.tim.evenmanagmentsystem.event.service;

import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.event.mapper.EventMapper;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.event.model.EventCategory;
import de.tim.evenmanagmentsystem.event.respository.EventRepository;
import de.tim.evenmanagmentsystem.security.exception.InvalidRequestException;
import de.tim.evenmanagmentsystem.security.exception.NotFoundException;
import de.tim.evenmanagmentsystem.security.exception.UserNotFoundException;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import de.tim.evenmanagmentsystem.venue.repository.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test-data-init", "test"})
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

    @Nested
    @DisplayName("createEvent tests")
    class CreateEventTests {

        @Test
        @DisplayName("Should create and return event when data is valid")
        void createEvent_withValidData_shouldCreateAndReturnEvent() {
            // Arrange
            Long organizerId = 1L;
            Organizer organizer = createTestOrganizer(organizerId);

            Long venueId = 2L;
            Venue venue = createTestVenue(venueId, 200);

            EventRequest request = createValidEventRequest(venueId);
            Event savedEvent = createTestEvent(request, venue, organizer);
            EventResponse expectedResponse = createExpectedResponse(savedEvent);

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
        @DisplayName("Should throw UserNotFoundException when organizer does not exist")
        void createEvent_withNonExistingOrganizer_shouldThrowException() {
            // Arrange
            Long organizerId = 999L;
            Long venueId = 2L;
            EventRequest request = createValidEventRequest(venueId);

            when(organizerRepository.findById(organizerId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () ->
                    eventService.createEvent(request, organizerId));

            verify(organizerRepository).findById(organizerId);
            verifyNoInteractions(eventRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when venue does not exist")
        void createEvent_withNonExistingVenue_shouldThrowException() {
            // Arrange
            Long organizerId = 1L;
            Organizer organizer = createTestOrganizer(organizerId);

            Long venueId = 2L;
            EventRequest request = createValidEventRequest(venueId);

            when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
            when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () ->
                    eventService.createEvent(request, organizerId));

            verify(organizerRepository).findById(organizerId);
            verify(venueRepository).findById(venueId);
            verifyNoInteractions(eventRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when start time is after end time")
        void createEvent_withWrongStartingAt_shouldThrowException() {
            // Arrange
            Long organizerId = 1L;
            Organizer organizer = createTestOrganizer(organizerId);

            Long venueId = 2L;
            Venue venue = createTestVenue(venueId, 200);

            EventRequest request = EventRequest.builder()
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
            assertThrows(InvalidRequestException.class, () ->
                    eventService.createEvent(request, organizerId));

            verify(organizerRepository).findById(organizerId);
            verify(venueRepository).findById(venueId);
            verifyNoInteractions(eventRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when event capacity exceeds venue capacity")
        void createEvent_withCapacityExceedingVenueCapacity_shouldThrowException() {
            // Arrange
            Long organizerId = 1L;
            Organizer organizer = createTestOrganizer(organizerId);

            Long venueId = 2L;
            Venue venue = createTestVenue(venueId, 200);

            EventRequest request = EventRequest.builder()
                    .title("Test Title")
                    .description("Test Description")
                    .startingAt(LocalDateTime.now().plusDays(1).plusHours(1))
                    .endingAt(LocalDateTime.now().plusDays(1).plusHours(2))
                    .venueId(venueId)
                    .capacity(201)
                    .categories(Set.of("CONCERT", "THEATER"))
                    .build();

            when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
            when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));

            // Act & Assert
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                    eventService.createEvent(request, organizerId));

            assertEquals("Event capacity (201) cannot exceed venue capacity (200)", exception.getMessage());

            verify(organizerRepository).findById(organizerId);
            verify(venueRepository).findById(venueId);
            verifyNoInteractions(eventRepository);
        }
    }

    @Nested
    @DisplayName("updateEvent tests")
    class UpdateEventTests {

        @Test
        @DisplayName("Should update and return event when data is valid")
        void updateEvent_withValidData_shouldUpdateAndReturnEvent() {
            // Arrange
            Long organizerId = 1L;
            Organizer organizer = createTestOrganizer(organizerId);

            Long venueId = 2L;
            Venue venue = createTestVenue(venueId, 200);

            EventRequest request = createValidEventRequest(venueId);
            Event savedEvent = createTestEvent(request, venue, organizer);
            EventResponse expectedResponse = createExpectedResponse(savedEvent);

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

    }

    // Helper methods to create test objects
    private Organizer createTestOrganizer(Long id) {
        Organizer organizer = new Organizer();
        organizer.setId(id);
        organizer.setOrganizationName("Test Org");
        return organizer;
    }

    private Venue createTestVenue(Long id, int capacity) {
        Venue venue = new Venue();
        venue.setId(id);
        venue.setName("Test Venue");
        venue.setCapacity(capacity);
        return venue;
    }

    private EventRequest createValidEventRequest(Long venueId) {
        return EventRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .startingAt(LocalDateTime.now().plusDays(1))
                .endingAt(LocalDateTime.now().plusDays(2))
                .venueId(venueId)
                .capacity(100)
                .categories(Set.of("CONCERT", "THEATER"))
                .build();
    }

    private Event createTestEvent(EventRequest request, Venue venue, Organizer organizer) {
        Event event = new Event(
                request.getTitle(),
                request.getStartingAt(),
                request.getEndingAt(),
                venue,
                request.getCapacity(),
                organizer
        );
        event.setId(3L);
        event.setDescription(request.getDescription());
        event.setCategories(Set.of(EventCategory.THEATRE, EventCategory.CONCERT));
        return event;
    }

    private EventResponse createExpectedResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setCapacity(event.getCapacity());
        return response;
    }
}