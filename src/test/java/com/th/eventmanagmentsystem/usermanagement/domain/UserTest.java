package com.th.eventmanagmentsystem.usermanagement.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests für die User-Entity")
class UserTest {

    // Ein gültiger Dummy-BCrypt-Hash für die Tests (genau 60 Zeichen).
    private final String VALID_HASHED_PASSWORD = "$2a$10$N9qo8uLOickgx2ZMRZoMye.IKbeT_IuTL0Fp2aEMavFXrLbpIRH/O";

    @Nested
    @DisplayName("Konstruktor- und Setter-Validierung")
    class ConstructorAndSetterValidationTests {

        private User user;

        @BeforeEach
        void setUp() {
            user = new User();
        }

        @DisplayName("setPassword sollte bei ungültigen Eingaben eine Exception werfen")
        @ParameterizedTest(name = "Prüfe ungültiges Passwort: [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {
                " ", // Leerzeichen
                "kurz", // Zu kurz
                "ein-viel-zu-langer-string-der-definitiv-ueber-255-zeichen-geht-um-die-obere-grenze-der-datenbank-validierung-zu-testen-und-sicherzustellen-dass-unsere-logik-robust-ist-und-nicht-nur-auf-die-untere-grenze-prueft-sondern-auch-auf-die-obere-genau-wie-es-sein-sollte" // Zu lang
        })
        void setPassword_withInvalidInput_shouldThrowException(String invalidPassword) {
            // Assert: Wir erwarten, dass IMMER eine Exception geworfen wird.
            // Wir fangen die allgemeine Exception, da sowohl NullPointerException als auch IllegalArgumentException möglich sind.
            assertThrows(Exception.class, () -> {
                user.setPassword(invalidPassword);
            });
        }

        @Test
        @DisplayName("setPassword sollte ein gültiges, gehashtes Passwort akzeptieren")
        void setPassword_withValidHash_shouldSucceed() {
            // Assert: Wir erwarten, dass hier KEINE Exception geworfen wird.
            assertDoesNotThrow(() -> {
                user.setPassword(VALID_HASHED_PASSWORD);
            });
            assertEquals(VALID_HASHED_PASSWORD, user.getPassword());
        }

        @Test
        @DisplayName("Der Konstruktor sollte ungültige Passwörter ebenfalls abweisen")
        void constructor_withInvalidPassword_shouldThrowException() {
            // Arrange
            String invalidPassword = "kurz";
            UserProfile profile = null;
            String email = "test@example.com";

            // Act & Assert
            // Wir prüfen, ob die Validierungslogik auch im Konstruktor greift.
            assertThrows(IllegalArgumentException.class, () -> {
                new User(email, invalidPassword, profile);
            });
        }
    }

    @Nested
    @DisplayName("Rollenverwaltung")
    class RoleManagementTests {

        private User user;

        @BeforeEach
        void setUp() {
            // Wir erstellen einen User mit einem gültigen Zustand für die Rollen-Tests.
            user = new User("test@example.com", VALID_HASHED_PASSWORD, null);
        }

        @Test
        @DisplayName("addRole sollte eine Rolle korrekt zum Set hinzufügen")
        void addRole_shouldAddRoleToSet() {
            // Arrange
            assertFalse(user.hasRole(UserRole.ROLE_ADMIN), "Der User sollte die Rolle anfangs nicht haben.");

            // Act
            user.addRole(UserRole.ROLE_ADMIN);

            // Assert
            assertTrue(user.hasRole(UserRole.ROLE_ADMIN), "Der User sollte die Rolle jetzt haben.");
            assertEquals(1, user.getRoles().size());
        }

        @Test
        @DisplayName("removeRole sollte eine existierende Rolle entfernen")
        void removeRole_shouldRemoveExistingRole() {
            // Arrange
            user.addRole(UserRole.ROLE_USER);
            assertTrue(user.hasRole(UserRole.ROLE_USER));

            // Act
            boolean result = user.removeRole(UserRole.ROLE_USER);

            // Assert
            assertTrue(result, "removeRole sollte true zurückgeben, da die Rolle entfernt wurde.");
            assertFalse(user.hasRole(UserRole.ROLE_USER));
            assertTrue(user.getRoles().isEmpty());
        }

        @Test
        @DisplayName("addRole sollte bei null eine NullPointerException werfen")
        void addRole_withNull_shouldThrowException() {
            assertThrows(NullPointerException.class, () -> user.addRole(null));
        }
    }

    @Nested
    @DisplayName("Kapselung (Encapsulation)")
    class EncapsulationTests {

        @Test
        @DisplayName("getRoles sollte eine unveränderliche Kopie des Sets zurückgeben")
        void getRoles_shouldReturnUnmodifiableSet() {
            // Arrange
            User user = new User("test@example.com", VALID_HASHED_PASSWORD, Set.of(UserRole.ROLE_USER), null);

            // Act
            Set<UserRole> roles = user.getRoles();

            // Assert
            assertThrows(UnsupportedOperationException.class, () -> {
                roles.add(UserRole.ROLE_ADMIN);
            }, "Die zurückgegebene Rollenliste darf nicht veränderbar sein.");
        }
    }
}
