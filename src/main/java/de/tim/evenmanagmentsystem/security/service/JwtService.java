package de.tim.evenmanagmentsystem.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service für die Verarbeitung von JSON Web Tokens (JWT).
 * Verantwortlich für Generierung, Validierung und Extraktion von Informationen aus JWTs.
 */
@Service
@Slf4j
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    /**
     * Extrahiert den Benutzernamen (Subject) aus einem Token.
     *
     * @param token Das JWT, aus dem der Benutzername extrahiert werden soll
     * @return Der extrahierte Benutzername
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrahiert einen spezifischen Claim aus einem Token.
     *
     * @param token Das JWT, aus dem der Claim extrahiert werden soll
     * @param claimsResolver Eine Funktion, die den gewünschten Claim aus den Claims extrahiert
     * @return Der extrahierte Claim-Wert
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generiert ein Token für einen Benutzer ohne zusätzliche Claims.
     *
     * @param userDetails Die Benutzerdetails, für die das Token generiert werden soll
     * @return Das generierte JWT
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generiert ein Token mit zusätzlichen Claims.
     *
     * @param extraClaims Zusätzliche Claims, die in das Token aufgenommen werden sollen
     * @param userDetails Die Benutzerdetails, für die das Token generiert werden soll
     * @return Das generierte JWT
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Generiert ein Refresh-Token.
     *
     * @param userDetails Die Benutzerdetails, für die das Refresh-Token generiert werden soll
     * @return Das generierte Refresh-Token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Baut ein Token mit den angegebenen Claims, Benutzerdetails und Ablaufzeit.
     *
     * @param extraClaims Zusätzliche Claims, die in das Token aufgenommen werden sollen
     * @param userDetails Die Benutzerdetails, für die das Token generiert werden soll
     * @param expiration Die Ablaufzeit des Tokens in Millisekunden
     * @return Das gebaute JWT
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        // Berechne Ausstellungs- und Ablaufzeit
        Date issuedAt = new Date();
        Date expiryDate = new Date(issuedAt.getTime() + expiration);

        // Füge Standardclaims hinzu
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        // Baue und signiere das Token
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Überprüft, ob ein Token gültig ist.
     *
     * @param token Das zu überprüfende JWT
     * @param userDetails Die Benutzerdetails, gegen die das Token validiert werden soll
     * @return true, wenn das Token gültig ist, sonst false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Überprüft, ob ein Token abgelaufen ist.
     *
     * @param token Das zu überprüfende JWT
     * @return true, wenn das Token abgelaufen ist, sonst false
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Konvertiert das Ablaufdatum eines Tokens in LocalDateTime.
     *
     * @param token Das JWT, dessen Ablaufdatum konvertiert werden soll
     * @return Das Ablaufdatum als LocalDateTime
     */
    public LocalDateTime extractExpirationAsLocalDateTime(String token) {
        Date expirationDate = extractExpiration(token);
        return Instant.ofEpochMilli(expirationDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Extrahiert das Ablaufdatum aus einem Token.
     *
     * @param token Das JWT, aus dem das Ablaufdatum extrahiert werden soll
     * @return Das extrahierte Ablaufdatum
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrahiert alle Claims aus einem Token.
     *
     * @param token Das JWT, aus dem die Claims extrahiert werden sollen
     * @return Die extrahierten Claims
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse JWT token", e);
            throw e;
        }
    }

    /**
     * Generiert den Signaturschlüssel aus dem Secret-Key.
     *
     * @return Der generierte Signaturschlüssel
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}