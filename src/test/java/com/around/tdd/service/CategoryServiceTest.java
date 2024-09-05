package com.around.tdd.service;

import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.repository.CategoryRepository;

import com.around.tdd.vo.Category;
import com.around.tdd.vo.CategorySaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @DisplayName("카테고리 등록 성공")
    @Test
    void saveCategorySuccess() {
        // given
        long categorySeq = 1L;
        String name = "테스트 카테고리";
        int depth = 1;
        int sort = 1;

        Category category = Category.builder()
                                        .categorySeq(categorySeq)
                                        .name(name)
                                        .depth(depth)
                                        .sort(sort).build();

        CategorySaveRequest categoryRequest = createCategorySaveRequest(categorySeq, name, depth, sort);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // when
        Long savedCategorySeq = categoryService.saveCategory(categoryRequest);

        // then
        assertThat(savedCategorySeq).isEqualTo(categorySeq);
    }

    @DisplayName("카테고리 중복 등록 오류")
    @Test
    void duplicateCategoryFailed() {
        // given
        String name = "테스트 카테고리";
        CategorySaveRequest categoryRequest = createCategorySaveRequest(null, name, null, null);

        // when
        when(categoryRepository.existsByName(name)).thenReturn(true);

        // then
        assertThrows(DuplicateCategoryException.class, () -> categoryService.saveCategory(categoryRequest));
    }

    private CategorySaveRequest createCategorySaveRequest(Long categorySeq, String name, Integer sort, Integer depth) {
        CategorySaveRequest categoryRequest = new CategorySaveRequest();
        categoryRequest.setCategorySeq(categorySeq);
        categoryRequest.setName(name);
        categoryRequest.setSort(sort);
        categoryRequest.setDepth(depth);
        return categoryRequest;
    }
}