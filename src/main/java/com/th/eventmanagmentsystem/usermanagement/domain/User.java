package com.th.eventmanagmentsystem.usermanagement.domain;

import com.th.eventmanagmentsystem.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true, of = {"email"})
@ToString(exclude = {"password", "userRoles"})
public class User extends BaseEntity {

    @Email(message = "Das E-Mail-Format ist ungültig.")
    @NotBlank(message = "Die E-Mail-Adresse darf nicht leer sein.")
//    @UniqueEmail(message = "Diese E-Mail-Adresse wird bereits verwendet.")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Das Passwort darf nicht leer sein.")
    @Size(min = 60, max = 255, message = "Das gehashte Passwort muss zwischen 60 und 255 Zeichen lang sein (z.B. BCrypt).")
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "Der Benutzerstatus darf nicht null sein.")
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus status = UserStatus.INACTIVE; // Default

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_role_user"))
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(
            name = "user_profile_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_user_profile")
    )
    private UserProfile profile;

    /**
     * Konstruktor mit allen erforderlichen Feldern.
     * UserStatus wird standardmäßig auf ACTIVE gesetzt.
     * Keine Rollen werden standardmäßig zugewiesen.
     */
    public User(String email, String password, UserProfile profile) {
        this(email, password, UserStatus.INACTIVE, Collections.emptySet(), profile);
    }

    /**
     * Konstruktor mit Status aber ohne Rollen.
     */
    public User(String email, String password, UserStatus status, UserProfile profile) {
        this(email, password, status, Collections.emptySet(), profile);
    }

    /**
     * Konstruktor mit Rollen aber Standard-Status.
     */
    public User(String email, String password, Set<UserRole> roles, UserProfile profile) {
        this(email, password, UserStatus.INACTIVE, roles, profile);
    }

    /**
     * Vollständiger Konstruktor mit allen Feldern.
     */
    public User(String email, String password,
                UserStatus status, Set<UserRole> roles,
                UserProfile profile) {
        setEmail(email);
        setPassword(password);
        setStatus(status);
        setRoles(roles);
        setProfile(profile);
    }

    private void setProfile(UserProfile userProfile) {
        Objects.requireNonNull(userProfile, "User Profile cannot be null");

        if (this.profile != null && !this.profile.equals(userProfile)) {
            throw new IllegalStateException("This user is already assigned to a another User Profile");
        }

        if (userProfile.getUser() != null && !userProfile.getUser().equals(this)) {
            throw new IllegalArgumentException("User Profile is already assigned to another User");
        }

        this.profile = userProfile;

        if (userProfile.getUser() == null) {
            userProfile.setUser(this);
        }
    }

    // Rollenverwaltungsmethoden
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

    private void setRoles(Set<UserRole> userRoles) {
        Objects.requireNonNull(userRoles, "User Role List cannot be null");
        this.roles.clear();
        this.roles.addAll(userRoles);
    }

    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public void setPassword(String password) {
        Objects.requireNonNull(password, "Passowrd cannot be null");

        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < 60 || password.length() > 255) {
            throw new IllegalArgumentException("Das Passwort hat eine ungültige Länge (erwartet gehasht).");
        }

        this.password = password;
    }

    public void setEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");

        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        this.email = email;
    }

    public void setStatus(UserStatus userStatus) {
        Objects.requireNonNull(userStatus, "User Status cannot be null");
        this.status = userStatus;
    }
}
