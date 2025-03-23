package de.tim.evenmanagmentsystem.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO für Token-Erneuerungsanfragen.
 * Enthält das Refresh-Token, das für die Generierung eines neuen Access-Tokens verwendet wird.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    /**
     * Das Refresh-Token.
     * Darf nicht leer sein.
     */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}