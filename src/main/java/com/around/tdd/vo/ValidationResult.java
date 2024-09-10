package com.around.tdd.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationResult {
    private boolean valid;
    private String message;
}
