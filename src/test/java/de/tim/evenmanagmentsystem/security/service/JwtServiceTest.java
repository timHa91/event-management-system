package de.tim.evenmanagmentsystem.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String secretKey = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    private final long jwtExpiration = 900000; // 15 Minuten
    private final long refreshExpiration = 86400000; // 24 Stunden

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Setze die Werte für die Felder im JwtService manuell, da sie normalerweise über @Value injiziert werden
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        // Erstelle ein UserDetails-Objekt für Tests
        userDetails = User.withUsername("test@example.com")
                .password("password")
                .authorities("ROLE_ATTENDEE")
                .build();
    }

    @Test
    void shouldExtractUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals("test@example.com", extractedUsername);
    }

    @Test
    void shouldGenerateToken() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("test@example.com", jwtService.extractUsername(token));
    }

    @Test
    void shouldGenerateTokenWithExtraClaims() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        // When
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Then
        assertNotNull(token);
        assertEquals("customValue", jwtService.extractClaim(token, claims -> claims.get("customClaim")));
    }

    @Test
    void shouldGenerateRefreshToken() {
        // When
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Then
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
        assertEquals("test@example.com", jwtService.extractUsername(refreshToken));
    }

    @Test
    void shouldValidateToken() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldNotValidateTokenWithDifferentUser() {
        // Given
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = User.withUsername("other@example.com")
                .password("password")
                .authorities("ROLE_ATTENDEE")
                .build();

        // When
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldNotValidateExpiredToken() {
        // Given
        // Erstelle ein Token, das bereits abgelaufen ist
        String token = generateExpiredToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    /**
     * Hilfsmethode zum Generieren eines abgelaufenen Tokens für Tests
     */
    private String generateExpiredToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Abgelaufen
                .signWith(getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}