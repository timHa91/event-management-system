package com.th.eventmanagmentsystem.usermanagement.domain.model;

import com.th.eventmanagmentsystem.common.model.BaseEntity;
import com.th.eventmanagmentsystem.usermanagement.domain.exception.UserStatusUpdateException;
import com.th.eventmanagmentsystem.usermanagement.infrastructure.persistance.converter.EmailAddressConverter;
import com.th.eventmanagmentsystem.usermanagement.infrastructure.persistance.converter.UserPasswordConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true, of = {"email"})
@ToString(exclude = {"password", "roles"})
public class User extends BaseEntity {

    @Convert(converter = EmailAddressConverter.class)
    @Column(name = "email", nullable = false, unique = true)
    private EmailAddress email;

    @Convert(converter = UserPasswordConverter.class)
    @Column(name = "password", nullable = false)
    private UserPassword password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus status = UserStatus.INACTIVE; // Default

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_roles_user"))
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();

//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(
//            name = "user_profile_id",
//            referencedColumnName = "id",
//            nullable = false,
//            updatable = false,
//            foreignKey = @ForeignKey(name = "fk_user_profile")
//    )
//    private UserProfile profile;

    /**
     * Konstruktor mit allen erforderlichen Feldern.
     * UserStatus wird standardmäßig auf ACTIVE gesetzt.
     * Keine Rollen werden standardmäßig zugewiesen.
     */
    public User(EmailAddress email, UserPassword password) {
        this(email, password, UserStatus.INACTIVE, Collections.emptySet());
    }

    /**
     * Konstruktor mit Status aber ohne Rollen.
     */
    public User(EmailAddress email, UserPassword password, UserStatus status) {
        this(email, password, status, Collections.emptySet());
    }

    /**
     * Konstruktor mit Rollen aber Standard-Status.
     */
    public User(EmailAddress email, UserPassword password, Set<UserRole> roles) {
        this(email, password, UserStatus.INACTIVE, roles);
    }

    /**
     * Vollständiger Konstruktor mit allen Feldern.
     */
    public User(EmailAddress email, UserPassword password,
                UserStatus status, Set<UserRole> roles
    ) {
        this.email = email;
        this.password = password;

        this.status = Objects.requireNonNull(status, "UserStatus darf nicht null sein");
        this.roles = new HashSet<>(Objects.requireNonNull(roles, "Rollen dürfen nicht null sein"));
    }

    // --- Rollenverwaltungsmethoden ---
    public boolean hasRole(UserRole userRole) {
        return this.roles.contains(userRole);
    }

    public void addRole(UserRole userRole) {
        Objects.requireNonNull(userRole, "User Role cannot be null");
        this.roles.add(userRole);
    }

    public boolean removeRole(UserRole userRole) {
        Objects.requireNonNull(userRole, "User Role cannot be null");
        return this.roles.remove(userRole);
    }

    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    // --- Statusverwaltungsmethoden ---
    public void updateStatus(UserStatus newStatus) {
        Objects.requireNonNull(newStatus, "User Status cannot be null");

        if (this.status == newStatus) return;

        switch (this.status) {
            case INACTIVE -> {
                if (newStatus != UserStatus.ACTIVE) {
                    throw new UserStatusUpdateException("Ungültiger Statuswechsel: " + this.status + " → " + newStatus);
                }
            }

            case ACTIVE -> {
                if (newStatus == UserStatus.INACTIVE) {
                    throw new UserStatusUpdateException("Aktive Benutzer können nicht inaktiv gesetzt werden");
                }
            }
            case SUSPENDED, EXPIRED, LOCKED -> {
                if (newStatus != UserStatus.ACTIVE && newStatus != UserStatus.DELETED) {
                    throw new UserStatusUpdateException("Ungültiger Statuswechsel: " + this.status + " → " + newStatus);
                }
            }

            case DELETED -> throw new UserStatusUpdateException("Gelöschte User können nicht geändert werden");
        }

        this.status = newStatus;
    }
}
