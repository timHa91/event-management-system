package de.tim.evenmanagmentsystem.event.respository;

import de.tim.evenmanagmentsystem.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByUuid(String uuid);
    Page<Event> findByOrganizerId(Long organizerId, Pageable pageable);

    @Query(value = "SELECT * FROM Event e WHERE e.venue.address.city = :city", nativeQuery = true)
    Page<Event> findByCity(@Param("city") String city, Pageable pageable);
}
