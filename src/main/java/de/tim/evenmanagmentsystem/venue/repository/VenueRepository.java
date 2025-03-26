package de.tim.evenmanagmentsystem.venue.repository;

import de.tim.evenmanagmentsystem.venue.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
}
