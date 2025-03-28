package de.tim.evenmanagmentsystem.ticket.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketTypeUpdateRequest {
    private String ticketCategory;

    private String description;

    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;

    private String currency;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private Integer maxPerOrder;

    private LocalDateTime salesStart;

    private LocalDateTime salesEnd;

    private boolean hasSeating;

    private BigDecimal serviceFee;

    private Integer minimumAge;
}
