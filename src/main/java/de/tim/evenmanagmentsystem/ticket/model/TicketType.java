package de.tim.evenmanagmentsystem.ticket.model;

import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.event.model.Event;
import de.tim.evenmanagmentsystem.ticket.exception.TicketSoldOutException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repräsentiert einen Ticket-Typ für ein Event.
 * Ein Ticket-Typ definiert die Kategorie, den Preis und die Verfügbarkeit von Tickets.
 */
@Entity
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

    /**
     * Standardkonstruktor für JPA
     */
    public TicketType() {
    }

    /**
     * Erstellt einen neuen Ticket-Typ mit den angegebenen Eigenschaften
     */
    public TicketType(@NotNull TicketCategory ticketCategory, @NotNull BigDecimal price,
                      @NotNull Currency currency, int quantity, Integer maxPerOrder,
                      @NotNull LocalDateTime salesStart, @NotNull LocalDateTime salesEnd,
                      boolean hasSeating, @NotNull Event event) {
        this.ticketCategory = ticketCategory;
        setPrice(price); // Verwende Setter für Validierung
        this.currency = currency;
        setQuantity(quantity); // Verwende Setter für Validierung
        this.maxPerOrder = maxPerOrder;
        this.salesStart = salesStart;
        this.salesEnd = salesEnd;
        this.hasSeating = hasSeating;
        this.ticketTypeStatus = TicketTypeStatus.ON_SALE;
        setEvent(event); // Verwende Setter für bidirektionale Beziehung
    }

    /**
     * Setzt das Event für diesen Ticket-Typ und aktualisiert die bidirektionale Beziehung
     */
    public void setEvent(Event event) {
        if (this.event != null && this.event != event) {
            this.event.getTicketTypes().remove(this);
        }
        this.event = event;

        if (event != null && !event.getTicketTypes().contains(this)) {
            event.getTicketTypes().add(this);
        }
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
     * @throws TicketSoldOutException wenn die Tickets nicht verfügbar sind oder die Menge das Limit überschreitet
     */
    public void purchase(int requestedQuantity) {
        if (canPurchase(requestedQuantity)) {
            sold += requestedQuantity;
            if (sold >= quantity) {
                this.ticketTypeStatus = TicketTypeStatus.SOLD_OUT;
            }
        } else {
            throw new TicketSoldOutException("Cannot purchase tickets: not available or quantity exceeds limit");
        }
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

    /**
     * Gibt die Anzahl der noch verfügbaren Tickets zurück
     */
    public int getAvailableQuantity() {
        return Math.max(0, quantity - sold);
    }

    // Getter und Setter

    public @NotNull TicketCategory getTicketCategory() {
        return ticketCategory;
    }

    public void setTicketCategory(@NotNull TicketCategory ticketCategory) {
        this.ticketCategory = ticketCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@NotNull BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public @NotNull Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull Currency currency) {
        this.currency = currency;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
        // Aktualisieren des Status, wenn die Menge geändert wird
        if (quantity <= sold && isActive()) {
            this.ticketTypeStatus = TicketTypeStatus.SOLD_OUT;
        } else {
            updateTicketStatus();
        }
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        if (sold < 0) {
            throw new IllegalArgumentException("Sold count cannot be negative");
        }
        if (sold > quantity) {
            throw new IllegalArgumentException("Sold count cannot exceed quantity");
        }
        this.sold = sold;
        // Aktualisieren des Status, wenn die verkaufte Menge geändert wird
        if (sold >= quantity && isActive()) {
            this.ticketTypeStatus = TicketTypeStatus.SOLD_OUT;
        } else {
            updateTicketStatus();
        }
    }

    public Integer getMaxPerOrder() {
        return maxPerOrder;
    }

    public void setMaxPerOrder(Integer maxPerOrder) {
        if (maxPerOrder != null && maxPerOrder <= 0) {
            throw new IllegalArgumentException("Max per order must be positive");
        }
        this.maxPerOrder = maxPerOrder;
    }

    public @NotNull LocalDateTime getSalesStart() {
        return salesStart;
    }

    public void setSalesStart(@NotNull LocalDateTime salesStart) {
        this.salesStart = salesStart;
        updateTicketStatus();
    }

    public @NotNull LocalDateTime getSalesEnd() {
        return salesEnd;
    }

    public void setSalesEnd(@NotNull LocalDateTime salesEnd) {
        this.salesEnd = salesEnd;
        updateTicketStatus();
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

    public void setServiceFee(BigDecimal serviceFee) {
        if (serviceFee != null && serviceFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Service fee cannot be negative");
        }
        this.serviceFee = serviceFee;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        if (minimumAge != null && minimumAge < 0) {
            throw new IllegalArgumentException("Minimum age cannot be negative");
        }
        this.minimumAge = minimumAge;
    }

    public TicketTypeStatus getTicketTypeStatus() {
        return ticketTypeStatus;
    }

    public void setTicketTypeStatus(TicketTypeStatus ticketTypeStatus) {
        this.ticketTypeStatus = ticketTypeStatus;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "TicketType{" +
                "ticketCategory=" + ticketCategory +
                ", price=" + price + " " + currency +
                ", available=" + getAvailableQuantity() + "/" + quantity +
                ", salesPeriod=" + salesStart + " to " + salesEnd +
                ", status=" + ticketTypeStatus +
                '}';
    }
}