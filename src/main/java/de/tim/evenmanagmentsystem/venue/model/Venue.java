package de.tim.evenmanagmentsystem.venue.model;

import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.event.model.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@ToString(exclude = "events")
@Table(name = "venue")
public class Venue extends BaseEntity {

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "venue", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    public Venue() {
    }

    public Venue(@NotBlank String name, @NotNull Address address,
                 @NotNull Double longitude, @NotNull Double latitude,
                 int capacity) {

        setName(name);
        setAddress(address);
        setLongitude(longitude);
        setLatitude(latitude);
        setCapacity(capacity);
    }

    public void addEvent(@NotNull Event event) {
        Objects.requireNonNull(event, "Event cannot be null");

        if (!events.contains(event)) {
            events.add(event);

            if (event.getVenue() != this) {
                event.setVenue(this);
            }
        }
    }

    /**
     * Entfernt ein Event aus dieser Venue.
     * ACHTUNG: Diese Methode sollte nur verwendet werden, wenn das Event
     * einer anderen Venue zugewiesen wird oder gelöscht werden soll.
     * Ein Event ohne Venue verletzt die Domänenregeln.
     */
    public void removeEvent(@NotNull Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
            events.remove(event);
            // WICHTIG: Wir setzen hier die Venue nicht auf null,
            // da ein Event immer eine Venue haben muss.
            // Diese Methode sollte nur im Kontext eines Venue-Wechsels
            // oder einer Event-Löschung aufgerufen werden.
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        // Prüfen, ob Events mit höherer Kapazität existieren
        for (Event event : events) {
            if (event.getCapacity() > capacity) {
                throw new IllegalArgumentException(
                        "Cannot reduce venue capacity below capacity of existing events");
            }
        }

        this.capacity = capacity;
    }

    public void setName(@NotBlank String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setAddress(@NotNull Address address) {
        this.address = Objects.requireNonNull(address, "Address cannot be null");
    }

    public void setLatitude(@NotNull Double latitude) {
        this.latitude = Objects.requireNonNull(latitude, "Latitude cannot be null");
    }

    public void setLongitude(@NotNull Double longitude) {
        this.longitude = Objects.requireNonNull(longitude, "Longitude cannot be null");
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public int getCapacity() {
        return capacity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}