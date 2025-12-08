package com.harshal.ticket_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Remark {
    private String message;
    private String createdBy;
    private LocalDateTime createdAt;
}
