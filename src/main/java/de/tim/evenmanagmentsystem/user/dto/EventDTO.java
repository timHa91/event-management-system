package de.tim.evenmanagmentsystem.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
  @NotBlank(message = "Title is required")
  private String title;

  @NotBlank(message = "Description is required")
  private String description;

  @NotNull(message = "Start date is required")
  private LocalDateTime startDate;

  @NotNull(message = "End date is required")
  private LocalDateTime endDate;

  @NotNull(message = "Location is required")
  private String location;

  @NotNull(message = "Capacity is required")
  private int capacity;

  @NotNull(message = "Organizer is required")
  private String organizer;

  @NotNull(message = "Category is required")
  private String category;

  @NotNull(message = "Price is required")
  private double price;

  @NotNull(message = "Currency is required")
  private String currency;

}
