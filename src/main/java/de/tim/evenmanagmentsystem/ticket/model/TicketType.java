package de.tim.evenmanagmentsystem.ticket.model;

import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.ticket.exception.TicketSoldOutException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Repräsentiert einen Ticket-Typ für ein Event.
 * Ein Ticket-Typ definiert die Kategorie, den Preis und die Verfügbarkeit von Tickets.
 */
@Entity
@ToString(exclude = "event")
@Table(name = "ticket_type")
public class TicketType extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_category", nullable = false)
    private TicketCategory ticketCategory;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "sold")
    private int sold = 0;

    @Column(name = "max_per_order")
    private Integer maxPerOrder; // Max. Anzahl pro Bestellung, null = unbegrenzt

    @NotNull
    @Column(name = "sales_start", nullable = false)
    private LocalDateTime salesStart;

    @NotNull
    @Column(name = "sales_end", nullable = false)
    private LocalDateTime salesEnd;

    @Column(name = "has_seating", nullable = false)
    private boolean hasSeating = false;

    @Column(name = "service_fee")
    private BigDecimal serviceFee;

    @Column(name = "minimum_age")
    private Integer minimumAge; // Mindestalter, null = keine Altersbeschränkung

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false)
    private TicketTypeStatus ticketTypeStatus = TicketTypeStatus.ON_SALE;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false, updatable = false)
    private Event event;

    public TicketType() {
    }

    public TicketType(@NotNull TicketCategory ticketCategory, @NotNull BigDecimal price,
                      @NotNull Currency currency, int quantity, Integer maxPerOrder,
                      @NotNull LocalDateTime salesStart, @NotNull LocalDateTime salesEnd,
                      boolean hasSeating, @NotNull Event event) {

        setTicketCategory(ticketCategory);
        setPrice(price);
        setCurrency(currency);
        setQuantity(quantity);
        setMaxPerOrder(maxPerOrder);
        setSalesStart(salesStart);
        setSalesEnd(salesEnd);

        // Validiere, dass salesEnd nach salesStart liegt
        if (salesEnd.isBefore(salesStart)) {
            throw new IllegalArgumentException("Sales end cannot be before sales start");
        }

        setHasSeating(hasSeating);
        setEvent(event);
    }

    /**
     * Setzt das Event für diesen Ticket-Typ und aktualisiert die bidirektionale Beziehung
     */
    public void setEvent(@NotNull Event event) {
        Objects.requireNonNull(event, "Event cannot be null");

        if (this.event != null && this.event != event) {
            this.event.getTicketTypes().remove(this);
        }
        this.event = event;

        event.getTicketTypes().add(this);
    }

    /**
     * Entfernt die Verbindung zum Event
     */
    public void removeEvent() {
        if (this.event != null) {
            this.event.getTicketTypes().remove(this);
            this.event = null;
        }
    }

    /**
     * Prüft, ob der Ticket-Typ aktiv ist (nicht storniert)
     */
    public boolean isActive() {
        return ticketTypeStatus != TicketTypeStatus.CANCELLED;
    }

    /**
     * Prüft, ob Tickets dieses Typs derzeit verfügbar sind
     */
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();

        return isActive() &&
                now.isAfter(salesStart) &&
                now.isBefore(salesEnd) &&
                sold < quantity;
    }

    /**
     * Prüft, ob die angegebene Anzahl von Tickets gekauft werden kann
     */
    public boolean canPurchase(int requestedQuantity) {
        if (requestedQuantity <= 0) {
            return false;
        }

        return isAvailable() &&
                sold + requestedQuantity <= quantity &&
                (maxPerOrder == null || requestedQuantity <= maxPerOrder);
    }

    /**
     * Kauft die angegebene Anzahl von Tickets
     *
     * @throws TicketSoldOutException wenn die Tickets nicht verfügbar sind oder die Menge das Limit überschreitet
     */
    public void purchase(int requestedQuantity) {
        if (!isAvailable()) {
            throw new TicketSoldOutException("Tickets are not available for purchase");
        }

        if (sold + requestedQuantity > quantity) {
            throw new TicketSoldOutException("Not enough tickets available. Requested: " + requestedQuantity +
                    ", Available: " + getAvailableQuantity());
        }

        if (maxPerOrder != null && requestedQuantity > maxPerOrder) {
            throw new TicketSoldOutException("Requested quantity exceeds maximum allowed per order (" + maxPerOrder + ")");
        }

        sold += requestedQuantity;
        updateStatus();
    }

    /**
     * Aktualisiert den Status des Ticket-Typs basierend auf dem aktuellen Zustand
     */
    public void updateTicketStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (!isActive()) {
            ticketTypeStatus = TicketTypeStatus.CANCELLED;
        } else if (sold >= quantity) {
            ticketTypeStatus = TicketTypeStatus.SOLD_OUT;
        } else if (now.isBefore(salesStart)) {
            ticketTypeStatus = TicketTypeStatus.NOT_YET_ON_SALE;
        } else if (now.isAfter(salesEnd)) {
            ticketTypeStatus = TicketTypeStatus.SALE_ENDED;
        } else {
            ticketTypeStatus = TicketTypeStatus.ON_SALE;
        }
    }

    /**
     * Zentrale Methode zur Aktualisierung des Status nach relevanten Änderungen
     */
    public void updateStatus() {
        updateTicketStatus();
    }

    /**
     * Berechnet den Gesamtpreis inklusive Servicegebühr
     */
    public BigDecimal getTotalPrice() {
        if (serviceFee == null) {
            return price;
        }
        return price.add(serviceFee);
    }

    /**
     * Prüft, ob der Verkauf derzeit aktiv ist
     */
    public boolean isSaleActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive() &&
                now.isAfter(salesStart) &&
                now.isBefore(salesEnd) &&
                ticketTypeStatus == TicketTypeStatus.ON_SALE;
    }

    public void setTicketCategory(@NotNull TicketCategory ticketCategory) {
        Objects.requireNonNull(ticketCategory, "Ticket Category cannot be null");
        this.ticketCategory = ticketCategory;
    }

    public void setPrice(@NotNull BigDecimal price) {
        Objects.requireNonNull(price, "Price cannot be null");
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
        // Aktualisieren des Status, wenn die Menge geändert wird
        updateStatus();
    }

    /**
     * Setzt die Anzahl der verkauften Tickets.
     * Diese Methode sollte nur für interne Zwecke oder Datenmigration verwendet werden.
     * Für normale Verkaufsvorgänge sollte purchase() verwendet werden.
     */
    protected void setSold(int sold) {
        if (sold < 0) {
            throw new IllegalArgumentException("Sold count cannot be negative");
        }
        if (sold > quantity) {
            throw new IllegalArgumentException("Sold count cannot exceed quantity");
        }
        this.sold = sold;
        updateStatus();
    }

    public void setMaxPerOrder(Integer maxPerOrder) {
        if (maxPerOrder != null && maxPerOrder <= 0) {
            throw new IllegalArgumentException("Max per order must be positive");
        }
        this.maxPerOrder = maxPerOrder;
    }

    public void setSalesStart(@NotNull LocalDateTime salesStart) {
        Objects.requireNonNull(salesStart, "Sales start cannot be null");
        this.salesStart = salesStart;

        // Überprüfe salesEnd, falls es bereits gesetzt ist
        if (this.salesEnd != null && this.salesEnd.isBefore(salesStart)) {
            throw new IllegalArgumentException("Sales end cannot be before sales start");
        }

        updateStatus();
    }

    public void setSalesEnd(@NotNull LocalDateTime salesEnd) {
        Objects.requireNonNull(salesEnd, "Sales end cannot be null");

        // Überprüfe salesStart, falls es bereits gesetzt ist
        if (this.salesStart != null && salesEnd.isBefore(this.salesStart)) {
            throw new IllegalArgumentException("Sales end cannot be before sales start");
        }

        this.salesEnd = salesEnd;
        updateStatus();
    }

    public void setServiceFee(BigDecimal serviceFee) {
        if (serviceFee != null && serviceFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Service fee cannot be negative");
        }
        this.serviceFee = serviceFee;
    }

    public void setMinimumAge(Integer minimumAge) {
        if (minimumAge != null && minimumAge < 0) {
            throw new IllegalArgumentException("Minimum age cannot be negative");
        }
        this.minimumAge = minimumAge;
    }

    public void setDescription(String description) {
        if (description != null && description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    public void setCurrency(@NotNull Currency currency) {
        Objects.requireNonNull(currency, "Currency cannot be null");
        this.currency = currency;
    }

    public void setTicketTypeStatus(TicketTypeStatus ticketTypeStatus) {
        Objects.requireNonNull(ticketTypeStatus, "Ticket type status cannot be null");
        this.ticketTypeStatus = ticketTypeStatus;
    }

    /**
     * Gibt die Anzahl der noch verfügbaren Tickets zurück
     */
    public int getAvailableQuantity() {
        return Math.max(0, quantity - sold);
    }

    // Getter
    public TicketCategory getTicketCategory() {
        return ticketCategory;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSold() {
        return sold;
    }

    public Integer getMaxPerOrder() {
        return maxPerOrder;
    }

    public LocalDateTime getSalesStart() {
        return salesStart;
    }

    public LocalDateTime getSalesEnd() {
        return salesEnd;
    }

    public boolean hasSeating() {
        return hasSeating;
    }

    public void setHasSeating(boolean hasSeating) {
        this.hasSeating = hasSeating;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public TicketTypeStatus getTicketTypeStatus() {
        return ticketTypeStatus;
    }

    public Event getEvent() {
        return event;
    }
}