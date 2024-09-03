package com.around.tdd.controller;

import com.around.tdd.service.CategoryService;
import com.around.tdd.vo.CategorySaveRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
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
        String name = "테스트 카테고리";
        int sort = 1;
        int depth = 1;

        CategorySaveRequestDto requestDto = new CategorySaveRequestDto();
        requestDto.setName(name);
        requestDto.setSort(sort);
        requestDto.setDepth(depth);

        // when & then
        mockMvc.perform(post(baseUrl + "/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("카테고리 저장 성공"));

    }

    @DisplayName("카테고리 검증 실패")
    @Test
    void saveCategoryValidationFailed() throws Exception {
        // given
        CategorySaveRequestDto requestDto = new CategorySaveRequestDto();

        // when & then
        mockMvc.perform(post(baseUrl + "/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("카테고리 검증 오류"))
                .andExpect(jsonPath("$.errors.length()").value(3));
    }

}