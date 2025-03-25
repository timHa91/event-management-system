package de.tim.evenmanagmentsystem.ticket.model;

import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@ToString(exclude = {"ticketType", "owner"})
@Table(name = "ticket")
public class Ticket extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Attendee owner;

    @NotNull
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @NotNull
    @Column(name = "ticket_code", nullable = false, unique = true, updatable = false)
    private String ticketCode;

    @Column(name = "checked_in", nullable = false)
    private boolean checkedIn = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status = TicketStatus.VALID;

    public Ticket() {
        generateTicketCode();
    }

    public Ticket(@NotNull TicketType ticketType, @NotNull Attendee owner,
                  boolean checkedIn, @NotNull LocalDateTime purchaseDate) {

        generateTicketCode();
        this.ticketType = ticketType;
        this.owner = owner;
        this.checkedIn = checkedIn;
        this.purchaseDate = purchaseDate;
    }

    public boolean checkIn() {
        if (!checkedIn && status == TicketStatus.VALID) {
            checkedIn = true;
            status = TicketStatus.USED;

            return true;
        }
        return false;
    }

    public boolean cancel() {
        if (status == TicketStatus.VALID) {
            status = TicketStatus.CANCELLED;
            return true;
        }
        return false;
    }

    private void generateTicketCode() {
        this.ticketCode = UUID.randomUUID().toString();
    }

    public void setOwner(@NotNull Attendee attendee) {
        Objects.requireNonNull(attendee, "Attendee cannot be null");
        //TODO: Attende bidirectional
            this.owner = attendee;
    }

    public void setTicketType(@NotNull TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public void setStatus(@NotNull TicketStatus status) {
        this.status = status;
    }

    public void setPurchaseDate(@NotNull LocalDateTime purchaseDate) {
        Objects.requireNonNull(purchaseDate, "Purchased time cannot be null");

        this.purchaseDate = purchaseDate;
    }


    public TicketType getTicketType() {
        return ticketType;
    }

    public Attendee getOwner() {
        return owner;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public enum TicketStatus {
        VALID,      // Gültiges Ticket
        USED,       // Bereits verwendet (eingecheckt)
        CANCELLED,  // Storniert
        EXPIRED     // Abgelaufen (nach Veranstaltungsende)
    }
}



