package de.tim.evenmanagmentsystem.security.model;

/**
 * Enum für die verschiedenen Token-Typen
 */
public enum TokenType {
    /**
     * Bearer-Token gemäß OAuth 2.0-Spezifikation
     * Wird im Authorization-Header mit dem Präfix Bearer verwendet
     */
    BEARER,

    /**
     * Refresh-Token zur Erneuerung von Access-Tokens
     */
    REFRESH
}