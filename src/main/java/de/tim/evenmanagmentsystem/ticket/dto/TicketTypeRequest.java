package de.tim.evenmanagmentsystem.ticket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketTypeRequest {
    @NotBlank(message = "Ticket category is required")
    private String ticketCategory;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "Currency is required")
    private String currency;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private Integer maxPerOrder;

    @NotNull(message = "Sales start date is required")
    private LocalDateTime salesStart;

    @NotNull(message = "Sales end date is required")
    private LocalDateTime salesEnd;

    private boolean hasSeating;

    private BigDecimal serviceFee;

    private Integer minimumAge;
}