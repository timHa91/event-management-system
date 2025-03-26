package de.tim.evenmanagmentsystem.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO für Token-Erneuerungsanfragen.
 * Enthält das Refresh-Token, das für die Generierung eines neuen Access-Tokens verwendet wird.
 */
@Data
public class RefreshTokenRequest {

    /**
     * Das Refresh-Token.
     * Darf nicht leer sein.
     */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}