package de.tim.evenmanagmentsystem.event.e2e;

import de.tim.evenmanagmentsystem.event.dto.EventRequest;
import de.tim.evenmanagmentsystem.event.dto.EventResponse;
import de.tim.evenmanagmentsystem.security.service.JwtService;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService tokenService; // Angenommen, du hast einen Service für JWT-Token-Generierung

    private String organizerToken;
    private String attendeeToken;
    private EventRequest validRequest;

    @BeforeEach
    void setUp() {
        // Erstelle Test-Benutzer, falls nötig und generiere Tokens
        setupTestUsers();

        // Erstelle eine gültige Event-Anfrage
        validRequest = new EventRequest();
        validRequest.setTitle("E2E Test Event");
        // Weitere Felder setzen...
    }

    private void setupTestUsers() {
        // Erstelle oder hole Organizer
        Organizer organizer = userRepository.findByEmail("e2e-organizer@test.com")
                .map(user -> (Organizer) user)
                .orElseGet(() -> {
                    Organizer newOrganizer = new Organizer();
                    newOrganizer.setEmail("e2e-organizer@test.com");
                    newOrganizer.setPassword(passwordEncoder.encode("password123"));
                    // Weitere Felder setzen...
                    newOrganizer.addRole(UserRole.ROLE_ORGANIZER);
                    return (Organizer) userRepository.save(newOrganizer);
                });

        // Erstelle oder hole Attendee
        Attendee attendee = userRepository.findByEmail("e2e-attendee@test.com")
                .map(user -> (Attendee) user)
                .orElseGet(() -> {
                    Attendee newAttendee = new Attendee();
                    newAttendee.setEmail("e2e-attendee@test.com");
                    newAttendee.setPassword(passwordEncoder.encode("password123"));
                    // Weitere Felder setzen...
                    newAttendee.addRole(UserRole.ROLE_ATTENDEE);
                    return (Attendee) userRepository.save(newAttendee);
                });

        // Generiere Tokens
        organizerToken = tokenService.generateToken(organizer);
        attendeeToken = tokenService.generateToken(attendee);
    }

    @Test
    void createEvent_asOrganizer_shouldCreateAndReturnEvent() {
        // Sende Anfrage mit Organizer-Token
        EventResponse response = RestAssured.given()
                .port(port)
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post("/api/events")
                .then()
                .statusCode(201)
                .extract().as(EventResponse.class);

        // Prüfe Antwort
        assertNotNull(response);
        assertEquals(validRequest.getTitle(), response.getTitle());
        // Weitere Assertions...
    }

    @Test
    void createEvent_asAttendee_shouldReturnForbidden() {
        RestAssured.given()
                .port(port)
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post("/api/events")
                .then()
                .statusCode(403);
    }

    @Test
    void createEvent_unauthenticated_shouldReturnUnauthorized() {
        RestAssured.given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post("/api/events")
                .then()
                .statusCode(401);
    }
}
