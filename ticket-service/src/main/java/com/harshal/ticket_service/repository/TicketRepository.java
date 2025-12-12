package com.harshal.ticket_service.repository;

import com.harshal.ticket_service.entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends MongoRepository<Ticket,String> {
    List<Ticket> findByCreatedBy(String userId);   // Customer: view own tickets

    List<Ticket> findByAssignedTo(String staffId); // Staff: view assigned tickets

    List<Ticket> findByStatus(String status);      // Admin: filter by status

    long countByAssignedTo(String staffEmail);
}
