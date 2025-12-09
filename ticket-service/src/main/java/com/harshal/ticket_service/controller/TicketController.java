package com.harshal.ticket_service.controller;

import com.harshal.ticket_service.dto.RemarkRequest;
import com.harshal.ticket_service.dto.TicketRequest;
import com.harshal.ticket_service.dto.TicketResponse;
import com.harshal.ticket_service.dto.Remark;
import com.harshal.ticket_service.security.SecurityUtil;
import com.harshal.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                            // returns JSON directly
@RequestMapping("/tickets")               // base path for customer/staff endpoints
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final SecurityUtil securityUtil;

    // CUSTOMER ONLY: Create ticket
    @PostMapping("/create-ticket")
    public ResponseEntity<TicketResponse> createTicket(
            @RequestBody TicketRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        // hard block here
        securityUtil.allowCustomerOnly(role);

        TicketResponse response = ticketService.createTicket(request, email);
        return ResponseEntity.status(201).body(response);
    }

    // CUSTOMER ONLY: Get own tickets
    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        securityUtil.allowCustomerOnly(role);
        return ResponseEntity.ok(ticketService.getMyTickets(email));
    }

    // ANY ROLE: Get ticket by ID (service does fine-grained checks)
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(
            @PathVariable String id,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        return ResponseEntity.ok(ticketService.getTicketById(id, email, role));
    }

    // STAFF or ADMIN: Update status
    @PatchMapping("/status/{id}")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestHeader("X-User-Role") String role
    ) {
        securityUtil.allowAdminOrStaff(role);
        return ResponseEntity.ok(ticketService.updateStatus(id, status));
    }

    // CUSTOMER + STAFF + ADMIN: Add remark
    @PostMapping("/remarks/{id}")
    public ResponseEntity<List<Remark>> addRemark(
            @PathVariable String id,
            @RequestBody RemarkRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        return ResponseEntity.ok(ticketService.addRemark(id, request, email, role));
    }

    // delete from here = only for owner/customer is possible if you want later
    // global admin delete is handled in AdminTicketController
}
