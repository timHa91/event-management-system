package de.tim.evenmanagmentsystem.security.e2e;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthenticationE2ETest extends BaseE2ETest {

        @BeforeEach
        public void setUp() {
                initRestAssured();
        }

        @Test
        public void testCompleteAuthenticationFlow() {
                // 1. Registriere einen neuen Benutzer
                String uniqueEmail = "user_" + UUID.randomUUID().toString() + "@example.com";
                String password = "Password123!";

                String accessToken = registerAttendee(uniqueEmail, password);

                // 2. Überprüfe, ob das Token gültig ist, indem du auf einen geschützten
                // Endpunkt zugreifst
                given()
                                .header("Authorization", "Bearer " + accessToken)
                                .when()
                                .get("/api/attendees/me")
                                .then()
                                .statusCode(200);

                // 3. Melde dich ab
                given()
                                .header("Authorization", "Bearer " + accessToken)
                                .when()
                                .post("/api/auth/logout")
                                .then()
                                .statusCode(200);

                // 4. Überprüfe, ob das Token nach der Abmeldung ungültig ist
                given()
                                .header("Authorization", "Bearer " + accessToken)
                                .when()
                                .get("/api/attendees/me")
                                .then()
                                .statusCode(401);

                // 5. Melde dich erneut an
                Map<String, String> loginRequest = new HashMap<>();
                loginRequest.put("email", uniqueEmail);
                loginRequest.put("password", password);

                Response loginResponse = given()
                                .contentType(ContentType.JSON)
                                .body(loginRequest)
                                .when()
                                .post("/api/auth/login")
                                .then()
                                .statusCode(200)
                                .extract()
                                .response();

                String newAccessToken = loginResponse.jsonPath().getString("accessToken");
                String refreshToken = loginResponse.jsonPath().getString("refreshToken");

                // 6. Überprüfe, ob das neue Token gültig ist
                given()
                                .header("Authorization", "Bearer " + newAccessToken)
                                .when()
                                .get("/api/attendees/me")
                                .then()
                                .statusCode(200);

                // 7. Erneuere das Token mit dem Refresh-Token
                Map<String, String> refreshRequest = new HashMap<>();
                refreshRequest.put("refreshToken", refreshToken);

                Response refreshResponse = given()
                                .contentType(ContentType.JSON)
                                .body(refreshRequest)
                                .when()
                                .post("/api/auth/refresh-token")
                                .then()
                                .statusCode(200)
                                .extract()
                                .response();

                String renewedAccessToken = refreshResponse.jsonPath().getString("accessToken");

                // 8. Überprüfe, ob das erneuerte Token gültig ist
                given()
                                .header("Authorization", "Bearer " + renewedAccessToken)
                                .when()
                                .get("/api/attendees/me")
                                .then()
                                .statusCode(200);
        }

        @Test
        @Disabled
        public void testRoleBasedAccess() {
                // 1. Registriere einen Teilnehmer
                String attendeeEmail = "attendee_" + UUID.randomUUID().toString() + "@example.com";
                String password = "Password123!";

                String attendeeToken = registerAttendee(attendeeEmail, password);

                // 2. Registriere einen Veranstalter
                String organizerEmail = "organizer_" + UUID.randomUUID().toString() + "@example.com";
                String organizerToken = registerOrganizer(organizerEmail, password);

                // 3. Teilnehmer kann auf Teilnehmer-Endpunkte zugreifen
                given()
                                .header("Authorization", "Bearer " + attendeeToken)
                                .when()
                                .get("/api/attendees/me")
                                .then()
                                .statusCode(200);

                // 4. Teilnehmer kann nicht auf Veranstalter-Endpunkte zugreifen
                given()
                                .header("Authorization", "Bearer " + attendeeToken)
                                .when()
                                .get("/api/organizers/events")
                                .then()
                                .statusCode(403);

                // 5. Veranstalter kann auf Veranstalter-Endpunkte zugreifen
                given()
                                .header("Authorization", "Bearer " + organizerToken)
                                .when()
                                .get("/api/organizers/events")
                                .then()
                                .statusCode(200);

                // 6. Veranstalter kann nicht auf Admin-Endpunkte zugreifen
                given()
                                .header("Authorization", "Bearer " + organizerToken)
                                .when()
                                .get("/api/admin/users")
                                .then()
                                .statusCode(403);
        }

        @Test
        public void testInvalidCredentials() {
                // Teste ungültige Anmeldedaten
                Map<String, String> invalidCredentials = new HashMap<>();
                invalidCredentials.put("email", "nonexistent@example.com");
                invalidCredentials.put("password", "wrongpassword");

                given()
                                .contentType(ContentType.JSON)
                                .body(invalidCredentials)
                                .when()
                                .post("/api/auth/login")
                                .then()
                                .statusCode(404); // Die Anwendung gibt 404 zurück, wenn der Benutzer nicht gefunden
                                                  // wird
        }

        @Test
        public void testRegistrationWithExistingEmail() {
                // 1. Registriere einen Benutzer
                String email = "duplicate_" + UUID.randomUUID().toString() + "@example.com";
                String password = "Password123!";

                registerAttendee(email, password);

                // 2. Versuche, einen weiteren Benutzer mit derselben E-Mail zu registrieren
                Map<String, Object> registrationRequest = new HashMap<>();
                registrationRequest.put("email", email);
                registrationRequest.put("password", password);
                registrationRequest.put("firstName", "Another");
                registrationRequest.put("lastName", "User");
                registrationRequest.put("phoneNumber", "9876543210");
                registrationRequest.put("dateOfBirth", "1995-05-05");

                given()
                                .contentType(ContentType.JSON)
                                .body(registrationRequest)
                                .when()
                                .post("/api/auth/register/attendee")
                                .then()
                                .statusCode(409)
                                .body("code", equalTo("EMAIL_EXISTS"));
        }
}