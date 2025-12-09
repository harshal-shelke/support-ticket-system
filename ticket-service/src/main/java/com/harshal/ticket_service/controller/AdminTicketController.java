package com.harshal.ticket_service.controller;

import com.harshal.ticket_service.dto.TicketResponse;
import com.harshal.ticket_service.security.SecurityUtil;
import com.harshal.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private final TicketService ticketService;
    private final SecurityUtil securityUtil;

    // ADMIN: View all tickets
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestHeader("X-User-Role") String role
    ) {
        securityUtil.allowAdminOnly(role);
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // ADMIN: View any ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(
            @PathVariable String id,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        securityUtil.allowAdminOnly(role);
        return ResponseEntity.ok(ticketService.getTicketById(id, email, role));
    }

    // ADMIN: Assign ticket to STAFF
    @PatchMapping("/assign/{id}")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable String id,
            @RequestParam String staffEmail,
            @RequestHeader("X-User-Role") String role
    ) {
        securityUtil.allowAdminOnly(role);
        return ResponseEntity.ok(ticketService.assignTicket(id, staffEmail));
    }

    // ADMIN: Delete ANY ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicketAdmin(
            @PathVariable String id,
            @RequestHeader("X-User-Role") String role
    ) {
        securityUtil.allowAdminOnly(role);
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("Ticket deleted successfully.");
    }
}
