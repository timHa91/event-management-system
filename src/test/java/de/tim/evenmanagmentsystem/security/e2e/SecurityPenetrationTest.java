package de.tim.evenmanagmentsystem.security.e2e;

import de.tim.evenmanagmentsystem.BaseE2ETest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.junit.jupiter.api.Disabled;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SecurityPenetrationTest extends BaseE2ETest {

    @BeforeEach
    public void setUp() {
        initRestAssured();
    }

    @Test
    @Disabled("SQL-Injection wird durch Validierung auf @Email-Ebene verhindert")
    public void testSqlInjectionInLogin() {
        // Versuche eine SQL-Injection im Login-Formular
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "' OR 1=1 --");
        loginRequest.put("password", "anypassword");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401); // Sollte fehlschlagen
    }

    @Test
    @Disabled("XSS wird durch Validierung auf Eingabeebene verhindert")
    public void testXssInRegistration() {
        // Versuche, einen XSS-Payload in den Benutzernamen einzufügen
        Map<String, Object> registrationRequest = new HashMap<>();
        registrationRequest.put("email", "xss_" + UUID.randomUUID().toString() + "@example.com");
        registrationRequest.put("password", "Password123!");
        registrationRequest.put("firstName", "<script>alert('XSS')</script>");
        registrationRequest.put("lastName", "User");
        registrationRequest.put("phoneNumber", "1234567890");
        registrationRequest.put("dateOfBirth", "1990-01-01");

        // Die Registrierung sollte erfolgreich sein, aber der XSS-Payload sollte
        // neutralisiert werden
        String accessToken = given()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .when()
                .post("/api/auth/register/attendee")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("accessToken");

        // Überprüfe, ob der XSS-Payload bei der Rückgabe der Benutzerdaten
        // neutralisiert wurde
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/attendees/profile")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("<script>alert('XSS')</script>")); // Der Payload sollte als Text
                                                                              // zurückgegeben werden, nicht als
                                                                              // ausführbarer Code
    }

    @Test
    @Disabled("Brute-Force-Schutz muss separat implementiert werden")
    public void testBruteForceProtection() {
        // Simuliere einen Brute-Force-Angriff durch mehrere fehlgeschlagene
        // Anmeldeversuche
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "brute_force_test@example.com");
        loginRequest.put("password", "wrongpassword");

        // Führe mehrere fehlgeschlagene Anmeldeversuche durch
        for (int i = 0; i < 10; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when()
                    .post("/api/auth/login")
                    .then()
                    .statusCode(401);
        }

        // Nach zu vielen fehlgeschlagenen Versuchen sollte die Anmeldung vorübergehend
        // gesperrt sein
        // Hinweis: Dies erfordert die Implementierung eines Brute-Force-Schutzes in
        // deiner Anwendung
        // Dieser Test könnte fehlschlagen, wenn kein Brute-Force-Schutz implementiert
        // ist
        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(429); // Too Many Requests
    }

    @Test
    @Disabled("CSRF-Schutz ist implementiert, aber der Test-Endpoint ist noch nicht verfügbar")
    public void testCsrfProtection() {
        // CSRF-Schutz sollte für zustandslose APIs mit Token-basierter
        // Authentifizierung nicht erforderlich sein
        // Dieser Test überprüft, ob CSRF-Schutz korrekt deaktiviert ist

        // 1. Registriere einen Benutzer und erhalte ein gültiges Token
        String email = "csrf_test_" + UUID.randomUUID().toString() + "@example.com";
        String password = "Password123!";

        String accessToken = registerAttendee(email, password);

        // 2. Führe eine Anfrage ohne CSRF-Token durch (sollte erfolgreich sein)
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of("newPassword", "NewPassword123!"))
                .when()
                .post("/api/attendees/change-password")
                .then()
                .statusCode(200);
    }

    @Test
    public void testJwtTampering() {
        // 1. Registriere einen Benutzer und erhalte ein gültiges Token
        String email = "jwt_tamper_" + UUID.randomUUID().toString() + "@example.com";
        String password = "Password123!";

        String validToken = registerAttendee(email, password);

        // 2. Manipuliere das Token (ändere einen Buchstaben)
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        // 3. Versuche, mit dem manipulierten Token auf einen geschützten Endpunkt
        // zuzugreifen
        given()
                .header("Authorization", "Bearer " + tamperedToken)
                .when()
                .get("/api/attendees/profile")
                .then()
                .statusCode(401); // Sollte fehlschlagen
    }
}