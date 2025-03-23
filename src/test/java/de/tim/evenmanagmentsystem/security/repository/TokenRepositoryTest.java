package de.tim.evenmanagmentsystem.security.repository;

import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.model.TokenType;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.User;
import de.tim.evenmanagmentsystem.user.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindTokenByTokenString() {
        // Given
        User user = createAndPersistUser("test@example.com", "1");
        Token token = createAndPersistToken("token123", user, false, false);

        // When
        Optional<Token> foundToken = tokenRepository.findByToken("token123");

        // Then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("token123");
        assertThat(foundToken.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldNotFindTokenByNonExistentTokenString() {
        // Given
        User user = createAndPersistUser("test@example.com", "2");
        Token token = createAndPersistToken("token123", user, false, false);

        // When
        Optional<Token> foundToken = tokenRepository.findByToken("nonexistent");

        // Then
        assertThat(foundToken).isEmpty();
    }

    @Test
    void shouldFindAllValidTokensByUser() {
        // Given
        User user = createAndPersistUser("test@example.com", "3");

        // Erstelle 3 Token für den Benutzer, 2 gültig, 1 widerrufen
        Token validToken1 = createAndPersistToken("token1", user, false, false);
        Token validToken2 = createAndPersistToken("token2", user, false, false);
        Token revokedToken = createAndPersistToken("token3", user, true, false);

        // Erstelle ein Token für einen anderen Benutzer
        User otherUser = createAndPersistUser("other@example.com", "4");
        Token otherUserToken = createAndPersistToken("token4", otherUser, false, false);

        LocalDateTime now = LocalDateTime.now();

        // When
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId(), now);

        // Then
        assertThat(validTokens).hasSize(2);
        assertThat(validTokens).extracting(Token::getToken).containsExactlyInAnyOrder("token1", "token2");
    }

    @Test
    void shouldNotFindExpiredTokens() {
        // Given
        User user = createAndPersistUser("test@example.com", "5");

        // Erstelle ein gültiges und ein abgelaufenes Token
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(1);
        LocalDateTime future = now.plusDays(1);

        Token validToken = createAndPersistToken("valid", user, false, false);
        validToken.setExpiresAt(future);
        entityManager.persist(validToken);

        Token expiredToken = createAndPersistToken("expired", user, false, false);
        expiredToken.setExpiresAt(past);
        entityManager.persist(expiredToken);

        // When
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId(), now);

        // Then
        assertThat(validTokens).hasSize(1);
        assertThat(validTokens.get(0).getToken()).isEqualTo("valid");
    }

    // Hilfsmethoden

    private User createAndPersistUser(String email, String uniqueIdentifier) {
        var user = new Attendee(
                email,
                "password",
                "Test",
                "User",
                "12345678901" + uniqueIdentifier,  // Gültige Telefonnummer mit 11+ Ziffern
                LocalDate.of(1991, 10, 4),
                "address",
                "city",
                "postalCode",
                "country"
        );
        user.setUserStatus(UserStatus.ACTIVE);
        user.setActive(true);

        return entityManager.persist(user);
    }

    private Token createAndPersistToken(String tokenString, User user, boolean revoked, boolean expired) {
        Token token = Token.builder()
                .token(tokenString)
                .user(user)
                .tokenType(TokenType.BEARER)
                .revoked(revoked)
                .expired(expired)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        return entityManager.persist(token);
    }
}