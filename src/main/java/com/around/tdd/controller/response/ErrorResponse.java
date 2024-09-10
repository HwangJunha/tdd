package com.around.tdd.controller.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Getter
public final class ErrorResponse {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private final String requestPath;
    private final Map<String, String> errors;

    public ErrorResponse(int status, String message, LocalDateTime timestamp, String requestPath, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.requestPath = requestPath;
        this.errors = (errors != null) ? Collections.unmodifiableMap(errors) : Collections.emptyMap();
    }
}
