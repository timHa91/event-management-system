package de.tim.evenmanagmentsystem.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerRegistrationDTO extends UserRegistrationDTO {
    @NotBlank(message = "Organization name is required")
    private String organizationName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    @NotBlank(message = "Company registration number is required")
    private String companyRegistrationNumber;

    @NotBlank(message = "Bank account info is required")
    private String bankAccountInfo;
}
