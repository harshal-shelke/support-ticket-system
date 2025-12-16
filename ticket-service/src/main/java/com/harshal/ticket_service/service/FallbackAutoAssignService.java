package com.harshal.ticket_service.service;

import com.harshal.ticket_service.entity.Ticket;
import com.harshal.ticket_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class FallbackAutoAssignService {

    private final RestTemplate restTemplate;
    private final TicketRepository ticketRepository;

    private static final String AUTH_INTERNAL_STAFF_URL =
            "http://auth-service:8081/auth/internal/staff";

    public void assign(Ticket ticket) {

        List<Map<String, Object>> staffList =
                restTemplate.getForObject(AUTH_INTERNAL_STAFF_URL, List.class);

        if (staffList == null || staffList.isEmpty()) {
            log.warn("Fallback: no staff available");
            return;
        }

        Map<String, Object> staff =
                staffList.get(ThreadLocalRandom.current().nextInt(staffList.size()));


        String email = (String) staff.get("email");

        ticket.setAssignedTo(email);

        ticketRepository.save(ticket);

        log.info("Fallback assigned ticket {} to {}", ticket.getId(), email);
    }
}
