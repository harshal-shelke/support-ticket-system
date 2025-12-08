package com.harshal.ticket_service.entity;

import com.harshal.ticket_service.dto.Remark;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;

    private String title;
    private String description;
    private String priority;   // LOW, MEDIUM, HIGH
    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED

    private String createdBy;   // email of customer
    private String assignedTo;  // staff email (null initially)

    private LocalDateTime createdAt; // time of creation
    private LocalDateTime updatedAt; // time of last update

    private List<Remark> remarks;    // list of remarks updates
}
