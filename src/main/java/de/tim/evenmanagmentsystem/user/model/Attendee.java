package de.tim.evenmanagmentsystem.user.model;

import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.ticket.model.Ticket;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@ToString(exclude = "ticket")
@Table(name = "attendee")
@PrimaryKeyJoinColumn(name = "user_id")
public class Attendee extends User {

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

    @Column(name = "receive_notifications")
    private boolean receiveNotifications = false;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> ticket = new ArrayList<>();

    public Attendee() {

    }

    public Attendee(@NotBlank String email, @NotBlank String password, @NotBlank String firstName,
                    @NotBlank String lastName, @NotBlank String phoneNumber,
                    @NotNull LocalDate dateOfBirth) {
        super(email, password, firstName, lastName);
        setPhoneNumber(phoneNumber);
        setDateOfBirth(dateOfBirth);
    }

    // Validierung für zusammengehörige Felder
    @AssertTrue(message = "Emergency contact name and phone must both be provided or both be empty")
    private boolean isEmergencyContactValid() {
        return (emergencyContactName == null && emergencyContactPhone == null) ||
                (emergencyContactName != null && emergencyContactPhone != null);
    }

    public void addTicket(@NotNull Ticket ticket) {
        Objects.requireNonNull(ticket, "ticket cannot be null");
        if (ticket.getOwner() == this) {
            return;
        }
        this.ticket.add(ticket);
        ticket.setOwner(this);
    }

    public void removeTicket(@NotNull Ticket ticket) {
        Objects.requireNonNull(ticket, "ticket cannot be null");
        this.ticket.remove(ticket);
        ticket.setOwner(null);
    }

    public void setPhoneNumber(@NotBlank String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        this.phoneNumber = phoneNumber;
    }

    public void setDateOfBirth(@NotNull LocalDate dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be null or in the future");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        if (emergencyContactName != null && emergencyContactName.trim().isEmpty()) {
            throw new IllegalArgumentException("Emergency contact name cannot be empty");
        }
        this.emergencyContactName = emergencyContactName;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        if (emergencyContactPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Emergency contact phone cannot be empty");
        }
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public List<Ticket> getTicket() {
        return Collections.unmodifiableList(ticket);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())) // Behalte "ROLE_" Präfix
                .collect(Collectors.toList());
    }
}
