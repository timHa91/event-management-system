package de.tim.evenmanagmentsystem.user.dto;

import de.tim.evenmanagmentsystem.user.model.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateDTO {
    @NotNull(message = "Status is required")
    private UserStatus status;

    @NotNull(message = "Reason is required")
    private String reason;
}
