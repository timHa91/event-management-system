package com.th.eventmanagmentsystem.usermanagement.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "organizer_profile")
@NoArgsConstructor
@Getter
public class OrganizerProfile extends UserProfile {

    @Column(name = "organization_name", nullable = false, unique = true)
    private String organizationName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "website")
    private String website;

    @Column(name = "company_registration_number", nullable = false)
    private String companyRegistrationNumber;

    @Column(name = "bank_account_info", nullable = false)
    private String bankAccountInfo;

    @Column(name = "logo_url")
    private String logoUrl;


}
