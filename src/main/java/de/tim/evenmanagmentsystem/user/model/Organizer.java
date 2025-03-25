package de.tim.evenmanagmentsystem.user.model;

import de.tim.evenmanagmentsystem.event.model.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
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

    public Organizer(String email, String password, String firstName, String lastName, String organizationName, String description, String companyRegistrationNumber, String bankAccountInfo) {
        super(email, password, firstName, lastName);
        this.organizationName = organizationName;
        this.description = description;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.bankAccountInfo = bankAccountInfo;
    }

    public @NotBlank String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(@NotBlank String organizationName) {
        this.organizationName = organizationName;
    }

    public @NotBlank String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank String description) {
        this.description = description;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public @NotBlank String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public void setCompanyRegistrationNumber(@NotBlank String companyRegistrationNumber) {
        this.companyRegistrationNumber = companyRegistrationNumber;
    }

    public @NotBlank String getBankAccountInfo() {
        return bankAccountInfo;
    }

    public void setBankAccountInfo(@NotBlank String bankAccountInfo) {
        this.bankAccountInfo = bankAccountInfo;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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
