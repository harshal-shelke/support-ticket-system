package com.example.ticket_assigner_service.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoAssignService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String AUTH_INTERNAL_URL = "http://localhost:8081/auth/internal/staff";
    private final String TICKET_COUNT_URL = "http://localhost:8082/internal/tickets/assigned-count?staffEmail=";
    private final String TICKET_ASSIGN_URL = "http://localhost:8082/internal/tickets/assign/";

    // Main method to process a ticket-created event
    public void processTicketCreatedEvent(String eventJson) {
        try {
            JsonNode event = objectMapper.readTree(eventJson);
            String ticketId = event.get("ticketId").asText();
            String createdBy = event.get("createdBy").asText();

            log.info("🎯 Processing ticket {} created by {}", ticketId, createdBy);

            // Step 1: Fetch active staff list
            List<Map<String, Object>> staffList = fetchActiveStaff();
            if (staffList.isEmpty()) {
                log.warn("⚠ No active staff found. Cannot auto-assign ticket {}", ticketId);
                return;
            }

            // Step 2: Determine staff with minimum load
            String selectedStaff = pickLeastLoadedStaff(staffList);
            if (selectedStaff == null) {
                log.warn("⚠ Unable to find least-loaded staff for ticket {}", ticketId);
                return;
            }

            // Step 3: Assign ticket
            assignTicket(ticketId, selectedStaff);

        } catch (Exception e) {
            log.error("❌ Error processing ticket-created event: {}", e.getMessage());
        }
    }

    private List<Map<String, Object>> fetchActiveStaff() {
        try {
            List<Map<String, Object>> result =
                    restTemplate.getForObject(AUTH_INTERNAL_URL, List.class);

            log.info("👥 Active Staff: {}", result);
            return result;

        } catch (Exception e) {
            log.error("❌ Failed to fetch staff: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String pickLeastLoadedStaff(List<Map<String, Object>> staffList) {
        try {
            long minLoad = Long.MAX_VALUE;
            String selected = null;

            for (Map<String, Object> staff : staffList) {
                String email = staff.get("email").toString();

                long count = restTemplate.getForObject(
                        TICKET_COUNT_URL + email,
                        Long.class
                );

                log.info("📊 Staff {} has {} assigned tickets", email, count);

                if (count < minLoad) {
                    minLoad = count;
                    selected = email;
                }
            }

            log.info("🏆 Selected staff for assignment: {}", selected);
            return selected;

        } catch (Exception e) {
            log.error("❌ Error picking staff: {}", e.getMessage());
            return null;
        }
    }

    private void assignTicket(String ticketId, String staffEmail) {
        try {
            String url = TICKET_ASSIGN_URL + ticketId + "?staffEmail=" + staffEmail;

            restTemplate.postForObject(url, null, String.class);

            log.info("✅ Ticket {} assigned to {}", ticketId, staffEmail);

        } catch (Exception e) {
            log.error("❌ Failed to assign ticket {} → {}", ticketId, e.getMessage());
        }
    }
}
