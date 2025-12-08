package com.harshal.ticket_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketResponse {
    private String id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String createdBy;
    private String assignedTo;

    private LocalDateTime createdAt;   // ADD THIS
    private LocalDateTime updatedAt;   // ADD THIS

    private List<Remark> remarks;      // ADD THIS
}
