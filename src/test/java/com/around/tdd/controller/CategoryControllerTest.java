package com.around.tdd.controller;

import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.service.CategoryService;
import com.around.tdd.vo.CategoryResponse;
import com.around.tdd.vo.CategorySaveRequest;
import com.around.tdd.vo.CategorySearchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private final String baseUrl = "/api/v1/categories";

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

    @DisplayName("카테고리 단일 조회 성공")
    @Test
    void findOneCategorySuccess() throws Exception {
        // given
        Long categorySeq = 1L;

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategorySeq(categorySeq);
        categoryResponse.setDisplayYn('y');
        categoryResponse.setName("테스트 카테고리");
        categoryResponse.setDepth(1);
        categoryResponse.setSort(1);

        when(categoryService.findCategory(categorySeq)).thenReturn(categoryResponse);

        // when & then
        mockMvc.perform(get(baseUrl + "/" + categorySeq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.message").value("카테고리 단일 조회 성공"));

        verify(categoryService, times(1)).findCategory(categorySeq);
    }

    @DisplayName("유효하지 않은 카테고리 번호로 카테고리 단일 조회")
    @Test
    void findOneCategoryByInvalidCategorySeq() throws Exception {
        // given
        long categorySeq = -1L;

        // when & then
        mockMvc.perform(get(baseUrl + "/" + categorySeq))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @DisplayName("카테고리 목록 조회 성공")
    @Test
    void findCategoryListSuccess() throws Exception {
        // given
        CategoryResponse categoryResponse1 = new CategoryResponse();
        categoryResponse1.setCategorySeq(1L);
        categoryResponse1.setDisplayYn('y');
        categoryResponse1.setName("테스트 카테고리1");
        categoryResponse1.setDepth(1);
        categoryResponse1.setSort(1);

        CategoryResponse categoryResponse2 = new CategoryResponse();
        categoryResponse1.setCategorySeq(2L);
        categoryResponse1.setDisplayYn('y');
        categoryResponse1.setName("테스트 카테고리2");
        categoryResponse1.setDepth(1);
        categoryResponse1.setSort(2);

        CategorySearchRequest categorySearchRequest = new CategorySearchRequest();
        when(categoryService.findCategoryList(categorySearchRequest)).thenReturn(List.of(categoryResponse1, categoryResponse2));

        // when & then
        mockMvc.perform(get(baseUrl + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.message").value("카테고리 목록 조회 성공"));
    }
    
    @DisplayName("카테고리 단일 삭제 성공")
    @Test
    void deleteCategorySuccess() throws Exception {
        // given
        Long categorySeq = 1L;
        doNothing().when(categoryService).deleteCategory(categorySeq);

        // when & then
        mockMvc.perform(delete(baseUrl + "/" + categorySeq))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()))
                .andExpect(jsonPath("$.message").value("카테고리 단일 제거 성공"));
    }

    @DisplayName("존재하지 않는 카테고리 삭제 시도")
    @Test
    void deleteNotExistCategoryFail() throws Exception {
        // given
        Long notExistCategorySeq = 999L;
        doThrow(new EntityNotFoundException("카테고리를 찾을 수 없습니다.")).when(categoryService).deleteCategory(notExistCategorySeq);

        // when & then
        mockMvc.perform(delete(baseUrl + "/" + notExistCategorySeq))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("카테고리를 찾을 수 없습니다."));
    }

    @DisplayName("카테고리 전체 삭제 성공")
    @Test
    void deleteAllCategorySuccess() throws Exception {
        // when & then
        mockMvc.perform(delete(baseUrl + "/"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()))
                .andExpect(jsonPath("$.message").value("카테고리 전체 제거 성공"));
    }

    private CategorySaveRequest createCategorySaveRequest(String name, Integer sort, Integer depth) {
        CategorySaveRequest categoryRequest = new CategorySaveRequest();
        categoryRequest.setName(name);
        categoryRequest.setSort(sort);
        categoryRequest.setDepth(depth);
        return categoryRequest;
    }
}