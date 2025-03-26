package de.tim.evenmanagmentsystem.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class User extends BaseEntity implements UserDetails {

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(min = 8, max = 60)
    @NotBlank
    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();

    public User() {
    }

    public User(@NotBlank String email, @NotBlank String password,
                @NotBlank String firstName, @NotBlank String lastName) {

        setEmail(email);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
    }

    public void removeRole(UserRole role) {
        Objects.requireNonNull(role, "Role cannot be null");
        this.roles.remove(role);
    }

    public void setRoles(Set<UserRole> roles) {
        Objects.requireNonNull(roles, "roles must not be null");
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public void setEmail(@Email String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        this.email = email;
    }

    public void setFirstName(@NotBlank String firstName) {
        Objects.requireNonNull(firstName, "First name cannot be null");
        if (firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName;
    }

    public void setPassword(@Size(min = 8, max = 60) @NotBlank String password) {
        Objects.requireNonNull(password, "Password cannot be null");
        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }

    public void setUserStatus(@NotNull UserStatus userStatus) {
        Objects.requireNonNull(userStatus, "UserStatus cannot be null");
        this.userStatus = userStatus;
    }

    public void setLastName(@NotBlank String lastName) {
        Objects.requireNonNull(lastName, "Last name cannot be null");
        if (lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        this.lastName = lastName;
    }

    public void addRole(@NotNull UserRole role) {
        Objects.requireNonNull(role, "Role cannot be null");
        this.roles.add(role);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @JsonIgnore
    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean hasRole(UserRole role) {
        return this.roles.contains(role);
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return userStatus != UserStatus.EXPIRED;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return userStatus != UserStatus.LOCKED;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Oder Implementierung basierend auf einem Passwort-Ablaufdatum
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String toString() {
        return "User{" +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
