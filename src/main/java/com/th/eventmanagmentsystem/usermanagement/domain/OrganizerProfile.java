package com.th.eventmanagmentsystem.usermanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "organizer_profile")
@NoArgsConstructor
@Getter
public class OrganizerProfile extends UserProfile {

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


}
