package com.harshal.ticket_service.service;

import com.harshal.ticket_service.dto.RemarkRequest;
import com.harshal.ticket_service.dto.TicketRequest;
import com.harshal.ticket_service.dto.TicketResponse;
import com.harshal.ticket_service.dto.Remark;
import com.harshal.ticket_service.entity.Ticket;
import com.harshal.ticket_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service                   // marks this class as service (business logic layer)
@RequiredArgsConstructor   // creates constructor for all final fields (for DI)
public class TicketService {

    private final TicketRepository ticketRepository;

    // Create a new ticket for a user
    public TicketResponse createTicket(TicketRequest request, String userId) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedBy(userId);
        ticket.setAssignedTo(null);           // no staff yet
        ticket.setStatus("OPEN");             // default status
        ticket.setPriority("LOW");

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setRemarks(new ArrayList<>());

        Ticket saved = ticketRepository.save(ticket);
        return mapToResponse(saved);
    }

    // Get all tickets created by a user
    public List<TicketResponse> getMyTickets(String userId) {
        List<Ticket> tickets = ticketRepository.findByCreatedBy(userId);
        return tickets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get single ticket (can be useful later)
    public TicketResponse getTicketById(String id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return mapToResponse(ticket);
    }

    // Update ticket status (OPEN / IN_PROGRESS / RESOLVED / CLOSED)
    public TicketResponse updateStatus(String ticketId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);
        return mapToResponse(saved);
    }

    // Add remark to ticket
    public List<Remark> addRemark(String ticketId, RemarkRequest request, String userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getRemarks() == null) {
            ticket.setRemarks(new ArrayList<>());
        }

        Remark remark = new Remark();
        remark.setMessage(request.getMessage());
        remark.setCreatedBy(userId);
        remark.setCreatedAt(LocalDateTime.now());

        ticket.getRemarks().add(remark);
        ticket.setUpdatedAt(LocalDateTime.now());

        ticketRepository.save(ticket);

        return ticket.getRemarks();
    }

    // Mapper: converts entity -> DTO
    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse res = new TicketResponse();
        res.setId(ticket.getId());
        res.setTitle(ticket.getTitle());
        res.setDescription(ticket.getDescription());
        res.setStatus(ticket.getStatus());
        res.setCreatedBy(ticket.getCreatedBy());
        res.setPriority(ticket.getPriority());
        res.setAssignedTo(ticket.getAssignedTo());
        res.setCreatedAt(ticket.getCreatedAt());
        res.setUpdatedAt(ticket.getUpdatedAt());
        res.setRemarks(ticket.getRemarks());
        return res;
    }
}
