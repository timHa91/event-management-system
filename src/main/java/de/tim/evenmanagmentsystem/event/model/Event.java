package de.tim.evenmanagmentsystem.event.model;

import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.ticket.model.TicketType;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.venue.model.Venue;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@ToString(exclude = {"venue", "organizer", "ticketTypes"})
@Table(name = "event")
public class Event extends BaseEntity {

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 2000)
    @Column(name = "description", length = 2000)
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
    private Set<EventCategory> categories = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<TicketType> ticketTypes = new HashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @URL
    @Column(name = "image_url")
    private String imageUrl;

    public Event() {
    }

    public Event(@NotBlank String title, @NotNull LocalDateTime startingAt,
                 @NotNull LocalDateTime endingAt, @NotNull Venue venue,
                 int capacity, @NotNull Organizer organizer) {

        setTitle(title);
        setVenue(venue);
        setOrganizer(organizer);
        setStartingAt(startingAt);
        setEndingAt(endingAt);
        setCapacity(capacity);
    }

    /**
     * Setzt den organizer von diesem event und managed die bidirectional Beziehung
     */
    public void setOrganizer(@NotNull Organizer organizer) {
        Objects.requireNonNull(organizer, "Organizer cannot be null");

        if (this.organizer == organizer) {
            return;
        }

        if (this.organizer != null) {
            this.organizer.getEvents().remove(this);
        }

        this.organizer = organizer;
        organizer.getEvents().add(this);
    }

    /**
     * Setzt die venue für dieses event und managed die bidirectional Beziehung
     */
    public void setVenue(@NotNull Venue venue) {
        Objects.requireNonNull(venue, "Venue cannot be null");

        if (this.capacity > 0 && venue.getCapacity() < this.capacity) {
            throw new IllegalArgumentException(
                    String.format("Event capacity (%d) cannot exceed venue capacity (%d)",
                            this.capacity, venue.getCapacity()));
        }

        if (this.venue == venue) {
            return;
        }

        if (this.venue != null) {
            this.venue.getEvents().remove(this);
        }

        this.venue = venue;
        venue.getEvents().add(this);
    }

    public void removeVenue() {
        if (venue != null) {
            this.venue.getEvents().remove(this);
            this.venue = null;
        }
    }

    public void addCategory(@NotNull EventCategory category) {
        Objects.requireNonNull(category, "Category cannot be null");

        if (!this.categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(@NotNull EventCategory category) {
        Objects.requireNonNull(category, "Category cannot be null");
        this.categories.remove(category);
    }

    public void addTicketType(@NotNull TicketType ticketType) {
        Objects.requireNonNull(ticketType, "TicketType cannot be null");

        if (!this.ticketTypes.contains(ticketType)) {

            if (ticketType.getEvent() != null && ticketType.getEvent() != this) {
                ticketType.getEvent().removeTicketType(ticketType);
            }

            ticketTypes.add(ticketType);
            ticketType.setEvent(this);
        }
    }

    public void removeTicketType(@NotNull TicketType ticketType) {
        Objects.requireNonNull(ticketType, "TicketType cannot be null");
        this.ticketTypes.remove(ticketType);
    }

    public void setStartingAt(@NotNull LocalDateTime startingAt) {
        Objects.requireNonNull(startingAt, "Starting time cannot be null");

    if (this.endingAt != null && startingAt.isAfter(this.endingAt)) {
            throw new IllegalArgumentException("Starting at must be before ending at");
        }

        this.startingAt = startingAt;
    }

    public void setEndingAt(@NotNull LocalDateTime endingAt) {
        Objects.requireNonNull(endingAt, "Ending time cannot be null");

        if (this.startingAt != null && endingAt.isBefore(this.startingAt)) {
            throw new IllegalArgumentException("Ending time must be after starting time");
        }

        this.endingAt = endingAt;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        if (venue == null) {
            throw new IllegalArgumentException("Venue must be set before capacity can be validated");
        }

        if (venue.getCapacity() < capacity) {
            throw new IllegalArgumentException(
                    String.format("Event capacity (%d) cannot exceed venue capacity (%d)",
                            capacity, venue.getCapacity()));
        }

        this.capacity = capacity;
    }

    public void setImageUrl(@URL String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image url cannot be null or empty");
        }
        this.imageUrl = imageUrl;
    }

    public void setTitle(@NotBlank String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public void setDescription(@Size(max = 2000) String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description;
    }

    public void setCategories(Set<EventCategory> categories) {
        Objects.requireNonNull(categories, "Categories cannot be null");
        this.categories = categories;
    }

    // Getter-Methoden
    public Venue getVenue() {
        return venue;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartingAt() {
        return startingAt;
    }

    public LocalDateTime getEndingAt() {
        return endingAt;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<EventCategory> getCategories() {
        return categories;
    }

    public Set<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Organizer getOrganizer() {
        return organizer;
    }
}
