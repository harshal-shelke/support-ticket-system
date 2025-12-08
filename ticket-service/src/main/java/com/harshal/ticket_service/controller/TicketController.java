package com.harshal.ticket_service.controller;

import com.harshal.ticket_service.dto.RemarkRequest;
import com.harshal.ticket_service.dto.TicketRequest;
import com.harshal.ticket_service.dto.TicketResponse;
import com.harshal.ticket_service.dto.Remark;
import com.harshal.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                     // returns JSON automatically
@RequestMapping("/tickets")         // base URL for all ticket actions
@RequiredArgsConstructor            // inject final TicketService without @Autowired
public class TicketController {
    private final TicketService ticketService;

    // Create a new ticket (customer)
    @PostMapping("/create-ticket")
    public ResponseEntity<TicketResponse> createTicket(
            @RequestBody TicketRequest request,
            @RequestHeader("X-User-Id") String userId     // we will replace this with JWT later
    ) {
        TicketResponse response = ticketService.createTicket(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all tickets created by CURRENT USER
    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @RequestHeader("X-User-Id") String userId
    ) {
        List<TicketResponse> tickets = ticketService.getMyTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    // Get one ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable String id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    // Update ticket status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(ticketService.updateStatus(id, status));
    }

    // Add a remark
    @PostMapping("/{id}/remarks")
    public ResponseEntity<List<Remark>> addRemark(
            @PathVariable String id,
            @RequestBody RemarkRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(ticketService.addRemark(id, request, userId));
    }
}
