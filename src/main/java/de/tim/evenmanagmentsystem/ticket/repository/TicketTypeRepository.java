package de.tim.evenmanagmentsystem.ticket.repository;

import de.tim.evenmanagmentsystem.ticket.model.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {

    int findByEventId(Long id);
}
