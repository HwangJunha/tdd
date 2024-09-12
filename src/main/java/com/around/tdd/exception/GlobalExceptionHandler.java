package com.around.tdd.exception;

import com.around.tdd.controller.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        logger.error("MethodArgumentNotValidException occurred : {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return createErrorResponseEntity(HttpStatus.BAD_REQUEST,
                ex.getObjectName() + " 검증 오류",
                errors,
                request);
    }

    // IllegalArgumentException 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("IllegalArgumentException occurred : {}", ex.getMessage());

        return createErrorResponseEntity(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Map.of("error", ex.getMessage()),
                request);
    }

    // EntityNotFoundException 예외 처리
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        return createErrorResponseEntity(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                Map.of("error", ex.getMessage()),
                request);
    }

    // Exception 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        logger.error("{} occurred : {}", ex.getClass().getName(), ex.getMessage());
        return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
                "예기치 못한 오류 발생",
                Map.of("error", ex.getMessage()),
                request);
    }

    // 예외 응답 반환
    private ResponseEntity<ErrorResponse> createErrorResponseEntity(HttpStatus status, String message, Map<String, String> errors, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now(),
                request.getDescription(false),
                errors
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
