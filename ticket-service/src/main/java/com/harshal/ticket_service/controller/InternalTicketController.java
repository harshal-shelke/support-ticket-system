package com.harshal.ticket_service.controller;

import com.harshal.ticket_service.dto.TicketResponse;
import com.harshal.ticket_service.repository.TicketRepository;
import com.harshal.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/tickets")
@RequiredArgsConstructor
public class InternalTicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    // INTERNAL: assign ticket without admin role
    @PostMapping("/assign/{id}")
    public TicketResponse internalAssign(
            @PathVariable String id,
            @RequestParam String staffEmail
    ) {
        return ticketService.assignTicket(id, staffEmail);
    }

    // INTERNAL: Return count of tickets assigned to a staff
    @GetMapping("/assigned-count")
    public long getAssignedCount(@RequestParam String staffEmail) {
        return ticketRepository.countByAssignedTo(staffEmail);
    }
}
