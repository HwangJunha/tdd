package com.around.tdd.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ApiResponse {
    private Map<String, Object> data;
    private String message;
    private int status;

    public ApiResponse(Map<String, Object> data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
}
