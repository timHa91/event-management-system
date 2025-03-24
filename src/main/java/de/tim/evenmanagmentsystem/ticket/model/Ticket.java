package de.tim.evenmanagmentsystem.ticket.model;

import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
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
    }

    public Ticket(@NotNull TicketType ticketType, @NotNull Attendee owner, @NotBlank String ticketCode, boolean checkedIn, @NotNull LocalDateTime purchaseDate) {
        this.ticketType = ticketType;
        this.owner = owner;
        this.ticketCode = ticketCode;
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

    public void setTicketCode(@NotNull String ticketCode) {
        if (this.ticketCode == null) {
            this.ticketCode = ticketCode;
        }
    }

    public void setAttendee(@NotNull Attendee attendee) {
        if (this.owner == null) {
            this.owner = attendee;
        }
    }

    public @NotNull TicketType getTicketType() {
        return ticketType;
    }

    public @NotNull Attendee getOwner() {
        return owner;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public @NotNull String getTicketCode() {
        return ticketCode;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }


    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }


    @Override
    public String toString() {
        return "Ticket{" +
                "ticketType=" + ticketType +
                ", owner=" + owner +
                ", purchaseDate=" + purchaseDate +
                ", checkedIn=" + checkedIn +
                '}';
    }

     enum TicketStatus {
        VALID,      // Gültiges Ticket
        USED,       // Bereits verwendet (eingecheckt)
        CANCELLED,  // Storniert
        EXPIRED     // Abgelaufen (nach Veranstaltungsende)
    }
}



