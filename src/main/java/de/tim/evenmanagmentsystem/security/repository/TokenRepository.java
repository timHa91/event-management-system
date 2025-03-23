package de.tim.evenmanagmentsystem.security.repository;

import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    /**
     * Findet ein Token anhand des Token-Strings
     */
    Optional<Token> findByToken(String token);

    /**
     * Findet alle gültigen Tokens eines Benutzers
     */
    @Query("""
               SELECT t FROM Token t
               WHERE t.user.id = :userId
               AND t.expired = false
               AND t.revoked = false
               AND t.expiresAt > :now
            """)
    List<Token> findAllValidTokensByUser(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    /**
     * Findet alle gültigen Tokens eines bestimmten Typs für einen Benutzer
     */
    @Query("""
                SELECT t FROM Token t
                WHERE t.user.id = :userId
                AND t.tokenType = :type
                AND t.expired = false
                AND t.revoked = false
                AND t.expiresAt > :now
            """)
    List<Token> findAllValidTokensByUserAndType(
            @Param("userId") Long userId,
            @Param("tokenType") TokenType type,
            @Param("now") LocalDateTime now);

    /**
     * Markiert alle Tokens eines Benutzers als abgelaufen und widerrufen
     */
    @Query("""
                UPDATE Token t
                SET t.expired = true, t.revoked = true
                WHERE t.user.id = :userId
                AND t.expired = false
                AND t.revoked = false
            """)
    void revokeAllUserTokens(@Param("userId") Long userId);

    /**
     * Löscht alle abgelaufenen Tokens, die älter als der angegebene Zeitpunkt sind
     */
    void deleteByExpiredTrueAndExpiresAtBefore(LocalDateTime dateTime);
}
