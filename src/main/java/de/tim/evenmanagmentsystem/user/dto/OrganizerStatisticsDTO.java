package de.tim.evenmanagmentsystem.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerStatisticsDTO {
  private int totalEvents;
  private int totalTicketsSold;
  private double totalRevenue;
  private double averageRating;
  private int totalAttendees;
  private int totalOrganizers;
}
