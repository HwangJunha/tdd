package com.around.tdd.util;

import com.around.tdd.controller.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class HttpUtil {

    public static HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static <T> ResponseEntity<ApiResponse<T>>  createApiResponse(T body, String parameterName, String message, HttpStatus status) {
        if(body == null){
            return new ResponseEntity<>(new ApiResponse<>(Map.of(), message, status), HttpUtil.createJsonHeaders(), status);
        }
        return new ResponseEntity<>(new ApiResponse<>(Map.of(parameterName, body), message, status), HttpUtil.createJsonHeaders(), status);
    }
}
