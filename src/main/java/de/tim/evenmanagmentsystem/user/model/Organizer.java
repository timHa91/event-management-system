package de.tim.evenmanagmentsystem.user.model;

import de.tim.evenmanagmentsystem.event.model.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@ToString(exclude = "events")
@Table(name = "organizer")
@PrimaryKeyJoinColumn(name = "user_id")
public class Organizer extends User {
    @NotBlank
    @Column(name = "organization_name", nullable = false, unique = true)
    private String organizationName;

    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "contact_phone")
    private String contactPhone;

    @URL(message = "Website must be a valid URL")
    @Column(name = "website")
    private String website;

    @NotBlank
    @Column(name = "company_registration_number", nullable = false)
    private String companyRegistrationNumber;

    @NotBlank
    @Column(name = "bank_account_info", nullable = false)
    private String bankAccountInfo;

    @Column(name = "logo_url")
    private String logoUrl;

    @OneToMany(mappedBy = "organizer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();

    public Organizer() {
    }

    public Organizer(@NotBlank String email, @NotBlank String password,
                     @NotBlank String firstName, @NotBlank String lastName,
                     @NotBlank String organizationName, @NotBlank String description,
                     @NotBlank String companyRegistrationNumber, @NotBlank String bankAccountInfo) {
        super(email, password, firstName, lastName);
        setOrganizationName(organizationName);
        setDescription(description);
        setCompanyRegistrationNumber(companyRegistrationNumber);
        setBankAccountInfo(bankAccountInfo);
    }

    public void addEvent(@NotNull Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        if (event.getOrganizer() == this) {
            return;
        }

        events.add(event);
        event.setOrganizer(this);
    }

    public void removeEvent(@NotNull Event event) {
        Objects.requireNonNull(event, "Event cannot be null");

        events.remove(event);
    }

    public void setOrganizationName(@NotBlank String organizationName) {
        Objects.requireNonNull(organizationName, "organizationName cannot be null");
        if (!organizationName.matches("^[a-zA-Z0-9_\\- ]{3,}$")) {
            throw new IllegalArgumentException("Invalid organization name");
        }
        this.organizationName = organizationName;
    }

    public void setDescription(@NotBlank String description) {
        Objects.requireNonNull(description, "Description cannot be null");
        if (description.trim().isEmpty() || description.length() > 255) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    public void setCompanyRegistrationNumber(@NotBlank String companyRegistrationNumber) {
        Objects.requireNonNull(companyRegistrationNumber, "Company registration number cannot be null");
        if (!companyRegistrationNumber.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Company registration number is not a valid number");
        }
        this.companyRegistrationNumber = companyRegistrationNumber;
    }

    public void setBankAccountInfo(@NotBlank String bankAccountInfo) {
        Objects.requireNonNull(bankAccountInfo, "Bank account info cannot be null");
        if (!bankAccountInfo.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Bank account number is not a valid number");
        }
        this.bankAccountInfo = bankAccountInfo;
    }

    public void setLogoUrl(String logoUrl) {
        if (logoUrl != null && logoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("logoUrl cannot be empty");
        }
        this.logoUrl = logoUrl;
    }

    public void setContactPhone(String contactPhone) {
        if (contactPhone != null && !contactPhone.trim().isEmpty()) {
            if (!contactPhone.matches("^\\+?[0-9]{10,15}$")) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
        }
        this.contactPhone = contactPhone;
    }

    public void setWebsite(String website) {
        if (website != null && !website.trim().isEmpty()) {
            if (!website.matches("^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$")) {
                throw new IllegalArgumentException("Invalid website URL");
            }
        }
        this.website = website;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getDescription() {
        return description;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getWebsite() {
        return website;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public String getBankAccountInfo() {
        return bankAccountInfo;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Set<Event> getEvents() {
        return events;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())) // Behalte "ROLE_" Präfix
                .collect(Collectors.toList());
    }
}
