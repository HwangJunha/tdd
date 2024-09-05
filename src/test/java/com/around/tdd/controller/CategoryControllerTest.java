package com.around.tdd.controller;

import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.service.CategoryService;
import com.around.tdd.vo.CategorySaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/api/v1/category";

    @DisplayName("카테고리 저장 성공")
    @Test
    void saveCategorySuccess() throws Exception {
        // given
        CategorySaveRequest categoryRequest = createCategorySaveRequest("테스트 카테고리", 1, 1);

        // when & then
        mockMvc.perform(post(baseUrl + "/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.message").value("카테고리 저장 성공"));

    }

    @DisplayName("카테고리 검증 실패")
    @Test
    void saveCategoryValidationFailed() throws Exception {
        // given
        CategorySaveRequest requestDto = new CategorySaveRequest();

        // when & then
        mockMvc.perform(post(baseUrl + "/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("categorySaveRequest 검증 오류"))
                .andExpect(jsonPath("$.errors.length()").value(3));
    }

    @DisplayName("카테고리 중복 오류")
    @Test
    void duplicateCategoryFailed() throws Exception {
        // given
        when(categoryService.saveCategory(any(CategorySaveRequest.class)))
                .thenThrow(new DuplicateCategoryException("중복된 카테고리입니다."));

        CategorySaveRequest categoryRequest = createCategorySaveRequest("테스트 카테고리", 1, 1);

        // when & then
        mockMvc.perform(post(baseUrl + "/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("중복된 카테고리입니다."));

        // categoryService.saveCategory 메서드가 한번 호출되었는지 검증
        verify(categoryService, times(1)).saveCategory(any(CategorySaveRequest.class));
    }

    private CategorySaveRequest createCategorySaveRequest(String name, Integer sort, Integer depth) {
        CategorySaveRequest categoryRequest = new CategorySaveRequest();
        categoryRequest.setName(name);
        categoryRequest.setSort(sort);
        categoryRequest.setDepth(depth);
        return categoryRequest;
    }

}