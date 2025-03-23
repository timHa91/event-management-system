package de.tim.evenmanagmentsystem.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerUpdateDTO extends UserUpdateDTO {
  @NotBlank(message = "Organization name is required")
  private String organizationName;

  @NotBlank(message = "Description is required")
  private String description;

  @NotBlank(message = "Contact phone is required")
  private String contactPhone;

  @NotBlank(message = "Contact email is required")
  private String contactEmail;

  @NotBlank(message = "Website is required")
  private String website;

  @NotBlank(message = "Logo URL is required")
  private String logoUrl;
}
