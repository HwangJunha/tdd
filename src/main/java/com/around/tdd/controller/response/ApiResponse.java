package com.around.tdd.controller.response;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Getter
@ToString
public final class ApiResponse<T> {
    private final Map<String, T> data;
    private final String message;
    private final HttpStatus status;

    public ApiResponse(Map<String, T> data, String message, HttpStatus status) {
        this.data = Objects.requireNonNullElse(data, Collections.emptyMap());
        this.message = Objects.requireNonNull(message, "message cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
    }
}
