package de.tim.evenmanagmentsystem;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseE2ETest {

    @LocalServerPort
    private int port;

    @BeforeAll
    public static void setup() {
        // Deaktiviere das Standard-URL-Encoding von REST Assured
        RestAssured.urlEncodingEnabled = false;
    }

    /**
     * Initialisiert REST Assured mit dem zufälligen Port.
     */
    protected void initRestAssured() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    protected String getAccessToken(String email, String password) {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", email);
        loginRequest.put("password", password);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.jsonPath().getString("accessToken");
    }

    protected String registerAttendee(String email, String password) {
        Map<String, Object> registrationRequest = new HashMap<>();
        registrationRequest.put("email", email);
        registrationRequest.put("password", password);
        registrationRequest.put("firstName", "Test");
        registrationRequest.put("lastName", "User");

        String randomDigits = String.format("123%07d", (int) (Math.random() * 10000000));
        registrationRequest.put("phoneNumber", randomDigits);
        registrationRequest.put("dateOfBirth", "1990-01-01");
        registrationRequest.put("street", "Teststreet");
        registrationRequest.put("city", "Testcity");
        registrationRequest.put("country", "Testcountry");
        registrationRequest.put("zip", "12345");

        Response response = RestAssured.given()

                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .when()
                .post("/api/auth/register/attendee")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.jsonPath().getString("accessToken");
    }

    protected String registerOrganizer(String email, String password) {
        Map<String, Object> registrationRequest = new HashMap<>();
        registrationRequest.put("email", email);
        registrationRequest.put("password", password);
        registrationRequest.put("firstName", "Test");
        registrationRequest.put("lastName", "Organizer");
        registrationRequest.put("organizationName", "Test Organization " + System.currentTimeMillis());
        registrationRequest.put("description", "Test Description");
        registrationRequest.put("companyRegistrationNumber", "1234567890");
        registrationRequest.put("bankAccountInfo", "1234567890");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .when()
                .post("/api/auth/register/organizer")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.jsonPath().getString("accessToken");
    }
}