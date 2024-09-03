package com.around.tdd.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String requestPath;
    private Map<String, String> errors;

    public ErrorResponse(int status, String message, LocalDateTime timestamp, String requestPath, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.requestPath = requestPath;
        this.errors = errors;
    }
}
