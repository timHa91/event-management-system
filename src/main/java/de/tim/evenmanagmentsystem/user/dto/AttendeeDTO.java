package de.tim.evenmanagmentsystem.user.dto;

import de.tim.evenmanagmentsystem.common.model.Address;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeDTO extends UserDTO {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @Min(value = 0, message = "Age must be greater than 0")
    private int age; // Berechnetes Feld

    @NotNull(message = "Address is required")
    private Address address;

    @NotNull(message = "Receive notifications is required")
    private boolean receiveNotifications;

    // Optional: Zusammenfassende Informationen
    private int ticketCount;
}
