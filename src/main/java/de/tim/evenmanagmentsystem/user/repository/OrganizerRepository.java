package de.tim.evenmanagmentsystem.user.repository;

import de.tim.evenmanagmentsystem.user.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
}
