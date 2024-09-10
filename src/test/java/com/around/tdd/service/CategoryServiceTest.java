package com.around.tdd.service;

import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.repository.CategoryRepository;

import com.around.tdd.vo.Category;
import com.around.tdd.vo.CategoryResponse;
import com.around.tdd.vo.CategorySaveRequest;
import com.around.tdd.vo.CategorySearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        CategorySaveRequest categoryDto = new CategorySaveRequest();
        categoryDto.setCategorySeq(categorySeq);
        categoryDto.setName(name);
        categoryDto.setDepth(depth);
        categoryDto.setSort(sort);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // when
        Long savedCategorySeq = categoryService.saveCategory(categoryDto);

        // then
        assertThat(savedCategorySeq).isEqualTo(categorySeq);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @DisplayName("카테고리 중복 등록 오류")
    @Test
    void duplicateCategoryFailed() {
        // given
        String name = "테스트 카테고리";
        CategorySaveRequest categoryDto = new CategorySaveRequest();
        categoryDto.setName(name);

        when(categoryRepository.existsByName(name)).thenReturn(true);

        // when & then
        assertThrows(DuplicateCategoryException.class, () -> categoryService.saveCategory(categoryDto));
    }

    @DisplayName("카테고리 전체 목록 조회 성공")
    @Test
    void findCategoryListSuccess() {
        // given
        Category category1 = createCategoryEntity(1L, "테스트 카테고리1", 1, 1, 'y');
        Category category2 = createCategoryEntity(2L,"테스트 카테고리2", 2, 1, 'y');

        List<Category> categoryList = List.of(category1, category2);
        CategorySearchRequest searchRequest = new CategorySearchRequest();
        when(categoryRepository.findCategoryList(searchRequest)).thenReturn(categoryList);

        // when
        List<CategoryResponse> categoryResponseList = categoryService.findCategoryList(searchRequest);

        // then
        assertThat(categoryResponseList).isNotNull();
        assertThat(categoryResponseList.size()).isEqualTo(2);
        assertThat(categoryResponseList.getFirst().getName()).isEqualTo(category1.getName());
        verify(categoryRepository, times(1)).findCategoryList(searchRequest);
    }

    @DisplayName("카테고리 단일 조회 성공")
    @Test
    void findCategorySuccess() {
        // given
        Category category = createCategoryEntity(1L, "테스트 카테고리", 1, 1, 'n');

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategorySeq(category.getCategorySeq());
        categoryResponse.setName(category.getName());
        categoryResponse.setDepth(category.getDepth());
        categoryResponse.setSort(category.getSort());
        categoryResponse.setDisplayYn(category.getDisplayYn());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // when & then
        CategoryResponse findCategory = categoryService.findCategory(1L);
        assertThat(findCategory.getCategorySeq()).isEqualTo(categoryResponse.getCategorySeq());
        assertThat(findCategory.getName()).isEqualTo(categoryResponse.getName());
        assertThat(findCategory.getDepth()).isEqualTo(categoryResponse.getDepth());
        assertThat(findCategory.getSort()).isEqualTo(categoryResponse.getSort());
        assertThat(findCategory.getDisplayYn()).isEqualTo(categoryResponse.getDisplayYn());
        verify(categoryRepository, times(1)).findById(1L);
    }
    
    @DisplayName("카테고리 단일 조회 존재하지 않는 경우")
    @Test
    void findCategoryNotFound() {
        // given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> categoryService.findCategory(1L));
        verify(categoryRepository, times(1)).findById(1L);
    }

    // 카테고리 엔티티 생성
    private Category createCategoryEntity(Long categorySeq, String name, Integer sort, Integer depth, Character displayYn) {
        return Category.builder()
                .categorySeq(categorySeq)
                .name(name)
                .sort(sort)
                .depth(depth)
                .displayYn(displayYn)
                .build();
    }
}
