package com.th.eventmanagmentsystem.usermanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
public class AttendeeProfile extends UserProfile {

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @NotNull
    @Past
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Embedded
    @Column(name = "address")
    private Address address;

    @NotNull
    @Column(name = "receive_notifications")
    private Boolean receiveNotifications = false;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    public AttendeeProfile(String firstName, String lastName, String phoneNumber,
                           LocalDate dateOfBirth, Address address,
                           String emergencyContactName, String emergencyContactPhone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public AttendeeProfile(String firstName, String lastName, String phoneNumber,
                           LocalDate dateOfBirth, Address address) {
        this(firstName, lastName, phoneNumber, dateOfBirth,
                address, null, null);
    }

    public String getFullName() {
        return firstName + " " + lastName;
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
}
