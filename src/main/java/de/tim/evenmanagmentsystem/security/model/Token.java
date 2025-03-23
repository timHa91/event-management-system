package de.tim.evenmanagmentsystem.security.model;

import de.tim.evenmanagmentsystem.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Token is required")
    @Column(name = "token", unique = true, nullable = false, length = 500)
    private String token;

    @NotNull(message = "Token type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType = TokenType.BEARER;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "expired", nullable = false)
    private boolean expired = false;

    @NotNull(message = "Created at is required")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Expires at is required")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Überprüft, ob das Token gültig ist (nicht abgelaufen und nicht widerrufen)
     */
    @Transient // Nicht in der Datenbank gespeichert
    public boolean isValid() {
        return !expired && !revoked && expiresAt.isAfter(LocalDateTime.now());
    }
}
