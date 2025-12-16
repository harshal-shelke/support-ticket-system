package com.harshal.ticket_service.service;

import com.harshal.ticket_service.config.FeatureFlagConfig;
import com.harshal.ticket_service.dto.RemarkRequest;
import com.harshal.ticket_service.dto.TicketRequest;
import com.harshal.ticket_service.dto.TicketResponse;
import com.harshal.ticket_service.dto.Remark;
import com.harshal.ticket_service.entity.Ticket;
import com.harshal.ticket_service.exception.ApiException;
import com.harshal.ticket_service.repository.TicketRepository;
import com.harshal.ticket_service.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshal.ticket_service.kafka.TicketEventProducer;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service                   // service = business logic layer
@RequiredArgsConstructor   // constructor injection for final fields
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SecurityUtil securityUtil;
    private final TicketEventProducer eventProducer;
    private final ObjectMapper objectMapper;
    private final FallbackAutoAssignService fallbackAutoAssignService;
    private final FeatureFlagConfig featureFlagConfig;


    // Create a new ticket for a user (controller will ensure CUSTOMER only)
    public TicketResponse createTicket(TicketRequest request, String userEmail) {

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedBy(userEmail);
        ticket.setAssignedTo(null);           // no staff yet
        ticket.setStatus("OPEN");             // default status
        ticket.setPriority("LOW");

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setRemarks(new ArrayList<>());

        Ticket saved = ticketRepository.save(ticket);
        // Auto assignment logic (Kafka OR fallback)
        if (featureFlagConfig.isKafkaEnabled()) {

            // Kafka Event
            try {

                // Prepare event
                Map<String, Object> event = new HashMap<>();
                event.put("type", "TICKET_CREATED");
                event.put("ticketId", saved.getId());
                event.put("createdBy", userEmail);

                String eventJson = objectMapper.writeValueAsString(event);

                // Publish to Kafka
                eventProducer.sendTicketCreatedEvent(eventJson);

            } catch (Exception e) {
                System.out.println("❌ Error sending Kafka event: " + e.getMessage());
            }

        } else {
            // Fallback direct assignment (NO Kafka)
            fallbackAutoAssignService.assign(saved);
        }


        return mapToResponse(saved);
    }

    // Get all tickets created by a CUSTOMER (controller will ensure role)
    public List<TicketResponse> getMyTickets(String userEmail) {

        List<Ticket> tickets = ticketRepository.findByCreatedBy(userEmail);

        return tickets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get ticket by ID with ownership checks
    public TicketResponse getTicketById(String ticketId, String userEmail, String userRole) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ApiException("Ticket not found", HttpStatus.NOT_FOUND));

        // CUSTOMER -> can view only their own tickets
        if (securityUtil.isCustomer(userRole) &&
                !ticket.getCreatedBy().equalsIgnoreCase(userEmail)) {
            throw new ApiException("You can view only your own tickets", HttpStatus.FORBIDDEN);
        }

        // STAFF -> can view only assigned tickets
        if (securityUtil.isStaff(userRole) &&
                (ticket.getAssignedTo() == null ||
                        !ticket.getAssignedTo().equalsIgnoreCase(userEmail))) {
            throw new ApiException("This ticket is not assigned to you", HttpStatus.FORBIDDEN);
        }

        // ADMIN -> no restriction
        return mapToResponse(ticket);
    }

    // Update ticket status (called only by ADMIN/STAFF – controller checks)
    public TicketResponse updateStatus(String ticketId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ApiException("Ticket not found", HttpStatus.NOT_FOUND));

        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);
        return mapToResponse(saved);
    }

    public List<Remark> addRemark(
            String ticketId,
            RemarkRequest request,
            String userEmail,
            String userRole
    ) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ApiException("Ticket not found", HttpStatus.NOT_FOUND));

        // CUSTOMER → can remark only on own ticket
        if (securityUtil.isCustomer(userRole)) {
            if (!ticket.getCreatedBy().equalsIgnoreCase(userEmail)) {
                throw new ApiException("You can remark only on your own ticket", HttpStatus.FORBIDDEN);
            }
        }

        // STAFF → can remark only on assigned ticket
        if (securityUtil.isStaff(userRole)) {
            if (ticket.getAssignedTo() == null ||
                    !ticket.getAssignedTo().equalsIgnoreCase(userEmail)) {
                throw new ApiException("This ticket is not assigned to you", HttpStatus.FORBIDDEN);
            }
        }

        // ADMIN → always allowed (no restriction)

        if (ticket.getRemarks() == null) {
            ticket.setRemarks(new ArrayList<>());
        }

        Remark remark = new Remark();
        remark.setMessage(request.getMessage());
        remark.setCreatedBy(userEmail);
        remark.setCreatedAt(LocalDateTime.now());

        ticket.getRemarks().add(remark);
        ticket.setUpdatedAt(LocalDateTime.now());

        ticketRepository.save(ticket);

        return ticket.getRemarks();
    }

    // Delete ticket (who can delete is enforced by controller)
    public void deleteTicket(String ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ApiException("Ticket not found", HttpStatus.NOT_FOUND));

        ticketRepository.delete(ticket);
    }

    // ADMIN: Get ALL tickets
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ADMIN: Assign ticket to a staff
    public TicketResponse assignTicket(String ticketId, String staffEmail) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ApiException("Ticket not found", HttpStatus.NOT_FOUND));

        ticket.setAssignedTo(staffEmail);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);
        return mapToResponse(updated);
    }

    // entity -> DTO mapper
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
