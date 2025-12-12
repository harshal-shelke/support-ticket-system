package com.example.ticket_assigner_service.kafka;

import com.example.ticket_assigner_service.client.StaffClient;
import com.example.ticket_assigner_service.service.AutoAssignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketEventListener {

    private final AutoAssignService autoAssignService;

    @KafkaListener(topics = "ticket.events", groupId = "ticket-assigner-group")
    public void consume(String message) {
        log.info("📥 Received event → {}", message);

        autoAssignService.processTicketCreatedEvent(message);
    }
}
