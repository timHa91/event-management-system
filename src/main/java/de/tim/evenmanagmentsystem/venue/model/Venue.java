package de.tim.evenmanagmentsystem.venue.model;

import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.common.model.BaseEntity;
import de.tim.evenmanagmentsystem.event.model.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "venue")
public class Venue extends BaseEntity {

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "venue", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "venue", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();

    public Venue() {
    }

    public Venue(@NotBlank String name, @NotNull Address address) {
        this.name = name;
        this.address = address;
    }

    // Hilfsmethoden für Beziehungsverwaltung
    public void addEvent(Event event) {
        if (event != null) {
            events.add(event);
            event.setVenue(this);

            if (event.getVenue() != this) {
                event.setVenue(this);
            }
        }
    }

    public void removeEvent(Event event) {
        if (event != null && events.contains(event)) {
            events.remove(event);
            event.setVenue(null);
        }
    }

    public @NotBlank String getName() {
        return name;
    }

    public @NotNull Address getAddress() {
        return address;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public void setAddress(@NotNull Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", address=" + address +
                ", rooms=" + rooms +
                ", events=" + events +
                '}';
    }
}