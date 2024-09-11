package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.controller.response.ErrorResponse;
import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.service.CategoryService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.CategoryResponse;
import com.around.tdd.vo.CategorySaveRequest;
import com.around.tdd.vo.CategorySearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    
    private final HttpHeaders headers = HttpUtil.createJsonHeaders();

    /**
     * 카테고리 저장
     * @param saveRequest
     * @return
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Long>> saveCategory(@RequestBody @Valid CategorySaveRequest saveRequest) {
        
        // TODO 관리자 권한 확인 필요
        
        Long savedCategorySeq = categoryService.saveCategory(saveRequest);

        Map<String, Long> responseData = new HashMap<>();
        responseData.put("savedCategorySeq", savedCategorySeq);

        ApiResponse<Long> response = new ApiResponse<>(responseData, "카테고리 저장 성공", HttpStatus.CREATED);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    /**
     * 카테고리 전체 조회
     * @param searchRequest
     * @return
     */
    @GetMapping({"","/"})
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findCategoryList(@Valid CategorySearchRequest searchRequest) {
        List<CategoryResponse> categoryList = categoryService.findCategoryList(searchRequest);

        Map<String, List<CategoryResponse>> responseData = new HashMap<>();
        responseData.put("categoryList", categoryList);

        ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(responseData, "카테고리 목록 조회 성공", HttpStatus.OK);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * 카테고리 단일 조회
     * @param categorySeq
     * @return
     */
    @GetMapping("/{categorySeq}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findCategory(@PathVariable Long categorySeq) {
        validateCategorySeq(categorySeq);

        CategoryResponse category = categoryService.findCategory(categorySeq);

        Map<String, CategoryResponse> responseData = new HashMap<>();
        responseData.put("category", category);

        ApiResponse<CategoryResponse> response = new ApiResponse<>(responseData, "카테고리 단일 조회 성공", HttpStatus.OK);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * 카테고리 단일 제거
     * @param categorySeq
     * @return
     */
    @DeleteMapping("/{categorySeq}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categorySeq) {
        validateCategorySeq(categorySeq);

        categoryService.deleteCategory(categorySeq);

        ApiResponse<Void> response = new ApiResponse<>(null, "카테고리 단일 제거 성공", HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response, headers, HttpStatus.NO_CONTENT);
    }

    /**
     * 카테고리 전체 제거
     * @return
     */
    @DeleteMapping({"/", ""})
    public ResponseEntity<ApiResponse<Void>> deleteAllCategory() {
        categoryService.deleteAllCategory();

        ApiResponse<Void> response = new ApiResponse<>(null, "카테고리 전체 제거 성공", HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response, headers, HttpStatus.NO_CONTENT);
    }

    /**
     * 카테고리 번호 유효성 체크
     * @param categorySeq
     */
    private void validateCategorySeq(Long categorySeq) {
        if (categorySeq <= 0) {
            throw new IllegalArgumentException("잘못된 카테고리 번호");
        }
    }

    /**
     * 중복 카테고리 예외처리
     * @param ex
     * @param request
     * @return
     */
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
