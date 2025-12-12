package com.harshal.ticket_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketEventProducer {
    private final KafkaTemplate<String,String>kafkaTemplate;

    private static final String TOPIC="ticket.events";

    public void sendTicketCreatedEvent(String eventJson) {
        kafkaTemplate.send(TOPIC, eventJson);
        System.out.println("📤 Sent Ticket Created Event → " + eventJson);
    }
}
