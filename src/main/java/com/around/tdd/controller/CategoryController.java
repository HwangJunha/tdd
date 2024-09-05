package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.controller.response.ErrorResponse;
import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.service.CategoryService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.CategorySaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Long>> saveCategory(@RequestBody @Valid CategorySaveRequest categorySaveRequest) {
        
        // TODO 관리자 권한 확인 필요

        HttpHeaders headers = HttpUtil.createJsonHeaders();
        
        Long savedCategorySeq = categoryService.saveCategory(categorySaveRequest);

        Map<String, Long> responseData = new HashMap<>();
        responseData.put("savedCategorySeq", savedCategorySeq);

        ApiResponse<Long> response = new ApiResponse<>(responseData, "카테고리 저장 성공", HttpStatus.CREATED);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    // 중복 카테고리 예외처리
    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCategoryException(DuplicateCategoryException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "중복된 카테고리입니다.",
                LocalDateTime.now(),
                request.getDescription(false),
                Map.of("error", ex.getMessage())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

}
