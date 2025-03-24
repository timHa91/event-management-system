package de.tim.evenmanagmentsystem.user.dto;

import de.tim.evenmanagmentsystem.common.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeUpdateDTO extends UserUpdateDTO {
  @NotBlank(message = "Phone number is required")
  private String phoneNumber;

  @NotNull(message = "Date of birth is required")
  @Past(message = "Date of birth must be in the past")
  private LocalDate dateOfBirth;

  @NotNull(message = "Address is required")
  private Address address;

  @NotNull(message = "Receive notifications is required")
  private boolean receiveNotifications;
}
