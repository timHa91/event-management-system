package com.th.eventmanagmentsystem.usermanagement.model;

import com.th.eventmanagmentsystem.common.BasicEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
public class User extends BasicEntity {

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @Embedded
    private Address address;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.ACTIVE; // Default-Wert

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserRole> userRoles = new HashSet<>(); // Default-Wert

    /**
     * Konstruktor mit allen erforderlichen Feldern.
     * UserStatus wird standardmäßig auf ACTIVE gesetzt.
     * Keine Rollen werden standardmäßig zugewiesen.
     */
    public User(String email, String password, Address address,
                String firstName, String lastName) {
        this(email, password, address, firstName, lastName, UserStatus.ACTIVE, Collections.emptySet());
    }

    /**
     * Konstruktor mit Status aber ohne Rollen.
     */
    public User(String email, String password, Address address,
                String firstName, String lastName, UserStatus userStatus) {
        this(email, password, address, firstName, lastName, userStatus, Collections.emptySet());
    }

    /**
     * Konstruktor mit Rollen aber Standard-Status.
     */
    public User(String email, String password, Address address,
                String firstName, String lastName, Set<UserRole> roles) {
        this(email, password, address, firstName, lastName, UserStatus.ACTIVE, roles);
    }

    /**
     * Vollständiger Konstruktor mit allen Feldern.
     */
    public User(String email, String password, Address address,
                String firstName, String lastName, UserStatus userStatus, Set<UserRole> roles) {
        setEmail(email);
        setPassword(password);
        setAddress(address);
        setFirstName(firstName);
        setLastName(lastName);
        setUserStatus(userStatus);
        setUserRoles(roles);
    }

    // Rollenverwaltungsmethoden
    public boolean hasRole(UserRole userRole) {
        return this.userRoles.contains(userRole);
    }

    public void addRole(UserRole userRole) {
        Objects.requireNonNull(userRole, "User Role cannot be null");
        this.userRoles.add(userRole);
    }

    public boolean removeRole(UserRole userRole) {
        Objects.requireNonNull(userRole, "User Role cannot be null");
        return this.userRoles.remove(userRole);
    }

    private void setUserRoles(Set<UserRole> userRoles) {
        Objects.requireNonNull(userRoles, "User Role List cannot be null");
        this.userRoles.clear();
        this.userRoles.addAll(userRoles);
    }

    private Set<UserRole> getUserRoles() {
        return Collections.unmodifiableSet(this.userRoles);
    }


    private void setLastName(String lastName) {
        Objects.requireNonNull(lastName, "Last name cannot be null");
        if (lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        this.lastName = lastName;
    }

    private void setFirstName(String firstName) {
        Objects.requireNonNull(firstName, "First name cannot be null");
        if (firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName;
    }

    private void setAddress(Address address) {
        Objects.requireNonNull(address, "Address cannot be null");
        this.address = address;
    }

    private void setPassword(String password) {
        Objects.requireNonNull(password, "Passowrd cannot be null");
        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }

    private void setEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        this.email = email;
    }

    private void setUserStatus(UserStatus userStatus) {
        Objects.requireNonNull(userStatus, "User Status cannot be null");
        this.userStatus = userStatus;
    }
}
