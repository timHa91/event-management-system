package de.tim.evenmanagmentsystem.event.service;

import de.tim.evenmanagmentsystem.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
