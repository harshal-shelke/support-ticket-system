package com.harshal.ticket_service.dto;

import lombok.Data;

@Data
public class TicketRequest {
    private String title;
    private String description;
    private String priority;
}
