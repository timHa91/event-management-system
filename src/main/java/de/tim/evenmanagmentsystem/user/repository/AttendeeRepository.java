package de.tim.evenmanagmentsystem.user.repository;

import de.tim.evenmanagmentsystem.user.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
}
