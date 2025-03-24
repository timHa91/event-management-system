package de.tim.evenmanagmentsystem.event.model;

import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.ticket.model.TicketType;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "event")
public class Event extends BaseEntity {

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "starting_at", nullable = false)
    private LocalDateTime startingAt;

    @NotNull
    @Column(name = "ending_at", nullable = false)
    private LocalDateTime endingAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @ElementCollection
    @CollectionTable(name = "event_categories", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    private EnumSet<EventCategory> categories = EnumSet.noneOf(EventCategory.class);

    @OneToMany(mappedBy = "event", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<TicketType> ticketTypes = new HashSet<>();

    @URL
    @Column(name = "imageUrl")
    private String imageUrl;

    public Event() {
    }

    public Event(@NotBlank String title, @NotNull LocalDateTime startingAt, @NotNull LocalDateTime endingAt, @NotNull Venue venue, int capacity) {
        this.title = title;
        this.startingAt = startingAt;
        this.endingAt = endingAt;
        this.venue = venue;
        this.capacity = capacity;
    }

    // Setter für Venue mit Berücksichtigung der bidirektionalen Beziehung
    public void setVenue(Venue venue) {
        // Alte Beziehung entfernen, wenn vorhanden
        if (this.venue != null && venue != null && !this.venue.equals(venue)) {
            this.venue.getEvents().remove(this);
        }
        this.venue = venue;

        // Neue Beziehung setzen
        if (venue != null) {
            venue.getEvents().add(this);
        }
    }

    public void removeVenue() {
        if (venue != null) {
            this.venue.getEvents().remove(this);
            this.venue = null;
        }
    }

    public void addCategory(EventCategory category) {
        if (category != null && !this.categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(EventCategory category) {
        if (category != null) {
            this.categories.remove(category);
        }
    }

    public void addTicketType(TicketType ticketType) {
        if (ticketType != null && !this.ticketTypes.contains(ticketType)) {

            // Entferne TicketType von dem bereits zugeordneten Event, falls vorhanden
            if (ticketType.getEvent() != null && ticketType.getEvent() != this) {
                ticketType.getEvent().removeTicketType(ticketType);
            }

            ticketTypes.add(ticketType);
            ticketType.setEvent(this);
        }
    }

    public void removeTicketType(TicketType ticketType) {
        if (ticketType != null && this.ticketTypes.contains(ticketType)) {
            this.ticketTypes.remove(ticketType);

            // Entferne das Event, nur wenn es zum aktuellen(this) passt
            if (ticketType.getEvent() == this) {
                ticketType.setEvent(null);
            }
        }
    }

    public @NotNull Venue getVenue() {
        return venue;
    }

    public @NotBlank String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull LocalDateTime getStartingAt() {
        return startingAt;
    }

    public void setStartingAt(@NotNull LocalDateTime startingAt) {
        this.startingAt = startingAt;
    }

    public @NotNull LocalDateTime getEndingAt() {
        return endingAt;
    }

    public void setEndingAt(@NotNull LocalDateTime endingAt) {
        this.endingAt = endingAt;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public EnumSet<EventCategory> getCategories() {
        return categories;
    }

    public Set<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public @URL String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@URL String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Event{" +
                "ticketTypes=" + ticketTypes +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startingAt=" + startingAt +
                ", endingAt=" + endingAt +
                ", venue=" + venue +
                ", capacity=" + capacity +
                ", categories=" + categories +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
