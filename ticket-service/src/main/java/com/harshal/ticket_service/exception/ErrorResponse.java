package com.harshal.ticket_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;      // error message
    private int status;          // HTTP status code
    private Instant timestamp;   // when the error happened
}
