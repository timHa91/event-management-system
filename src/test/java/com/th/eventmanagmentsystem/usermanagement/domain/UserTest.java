package com.th.eventmanagmentsystem.usermanagement.domain;

import com.th.eventmanagmentsystem.usermanagement.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests für die User-Entity")
class UserTest {

    private final String VALID_HASHED_PASSWORD = "$2a$10$N9qo8uLOickgx2ZMRZoMye.IKbeT_IuTL0Fp2aEMavFXrLbpIRH/O";

    @Nested
    @DisplayName("Rollenverwaltung")
    class RoleManagementTests {

        private User user;

        @BeforeEach
        void setUp() {
            user = new User(EmailAddress.of("test@example.com"), UserPassword.of(VALID_HASHED_PASSWORD));
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
            User user = new User(EmailAddress.of("test@example.com"), UserPassword.of(VALID_HASHED_PASSWORD), Set.of(UserRole.ROLE_USER));

            // Act
            Set<UserRole> roles = user.getRoles();

            // Assert
            assertThrows(UnsupportedOperationException.class, () -> {
                roles.add(UserRole.ROLE_ADMIN);
            }, "Die zurückgegebene Rollenliste darf nicht veränderbar sein.");
        }
    }
}
