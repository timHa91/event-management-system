package de.tim.evenmanagmentsystem.user.repository;

import de.tim.evenmanagmentsystem.user.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
}
