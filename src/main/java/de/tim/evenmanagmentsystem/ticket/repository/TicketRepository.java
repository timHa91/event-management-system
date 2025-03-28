package de.tim.evenmanagmentsystem.ticket.repository;

import de.tim.evenmanagmentsystem.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}
