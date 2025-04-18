package de.tim.evenmanagmentsystem.event.e2e;

import de.tim.evenmanagmentsystem.BaseE2ETest;
import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import de.tim.evenmanagmentsystem.venue.repository.VenueRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class EventE2ETest extends BaseE2ETest {
    @Autowired
    private VenueRepository venueRepository;

    private String organizerToken;
    private String attendeeToken;
    private EventRequest validRequest;
    private Venue testVenue;

    @BeforeEach
    void setUp() {
        initRestAssured();

        organizerToken = registerOrganizer("organizer@test.com", "password");
        attendeeToken = registerAttendee("attendee@test.com", "password");

        testVenue = createTestVenue();

        validRequest = createValidEventRequest(testVenue.getId());
    }

    @Test
    @DisplayName("Organizer sollte ein Event erstellen können")
    void createEvent_asOrganizer_shouldCreateAndReturnEvent() {
        EventResponse response = RestAssured.given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post("/api/events")
                .then()
                .log().ifValidationFails() // Nur loggen, wenn der Test fehlschlägt
                .statusCode(201)
                .body("title", equalTo(validRequest.getTitle()))
                .body("description", equalTo(validRequest.getDescription()))
                .body("capacity", equalTo(validRequest.getCapacity()))
                .body("id", notNullValue())
                .extract().as(EventResponse.class);

        assertNotNull(response);
        assertEquals(validRequest.getTitle(), response.getTitle());
        assertEquals(validRequest.getCapacity(), response.getCapacity());
    }

    @Test
    @DisplayName("Attendee sollte kein Event erstellen dürfen")
    void createEvent_asAttendee_shouldReturnForbidden() {
        RestAssured.given()
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post("/api/events")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("Authentication failed: Insufficient permissions to access this resource"));
    }

    @Test
    @DisplayName("Authentifizierte Anfragen sollten abgelehnt werden")
    void createEvent_unauthenticated_shouldReturnUnauthorized() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post("/api/events")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("Authentication required: No valid token provided"));
    }

    @Test
    @DisplayName("Event mit ungültigen Daten sollte abgelehnt werden")
    void createEvent_withInvalidData_shouldReturnBadRequest() {
        EventRequest invalidRequest = validRequest;
        invalidRequest.setTitle("");

        RestAssured.given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/events")
                .then()
                .log().ifValidationFails()
                .statusCode(400);  // Bad Request erwartet
    }

    @Test
    @DisplayName("Event mit ungültigen Daten sollte abgelehnt werden")
    void createEvent_withInvalidDates_shouldReturnBadRequest() {
        EventRequest invalidRequest = validRequest;
        invalidRequest.setStartingAt(LocalDateTime.now().plusDays(2).plusHours(2));
        invalidRequest.setStartingAt(LocalDateTime.now().plusDays(2).plusHours(1));

        RestAssured.given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/events")
                .then()
                .log().ifValidationFails()
                .statusCode(400);
    }

    @Test
    @DisplayName("Event mit ungültigen Capacity Daten sollte abgelehnt werden")
    void createEvent_withInvalidCapacity_shouldReturnBadRequest() {
        EventRequest invalidRequest = validRequest;
        invalidRequest.setCapacity(121);

        RestAssured.given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/events")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("message", equalTo(String.format("Event capacity (%d) cannot exceed venue capacity (%d)", invalidRequest.getCapacity(), testVenue.getCapacity())));
    }

    // Hilfsmethoden für die Testdatenerstellung

    private Venue createTestVenue() {
        Venue venue = new Venue(
                "Test Venue",
                new Address(
                        "Test City",
                        "Test Street",
                        "Germany",
                        "9090"
                ),
                12.345,
                45.678,
                120
        );

        return venueRepository.save(venue);
    }


    private EventRequest createValidEventRequest(Long venueId) {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        return EventRequest.builder()
                .title("Test Event")
                .description("Test description")
                .capacity(100)
                .startingAt(startTime)
                .endingAt(startTime.plusHours(3))
                .categories(Set.of("FESTIVAL", "ELECTRONIC"))
                .venueId(venueId)
                .imageUrl("http://example.com/image.jpg")
                .build();
    }
}