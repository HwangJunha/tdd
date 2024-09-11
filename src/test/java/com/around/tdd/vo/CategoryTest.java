package com.around.tdd.vo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    public void setUp() {
        // given
        parentCategory = new Category();
        childCategory = new Category();
    }

    @DisplayName("자식 카테고리 추가")
    @Test
    void addChildCategory() {
        // when
        parentCategory.addChildCategory(childCategory);

        // then
        assertThat(parentCategory.getChildCategoryList().contains(childCategory)).isTrue();
        assertThat(childCategory.getParentCategory()).isEqualTo(parentCategory);
    }

    @DisplayName("이미 존재하는 카테고리 추가 시도")
    @Test
    void addExistingChildCategory() {
        // given
        parentCategory.addChildCategory(childCategory);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parentCategory.addChildCategory(childCategory));
        assertEquals("이미 존재하는 하위 카테고리입니다.", exception.getMessage());
    }
    
    @DisplayName("자식 카테고리 추가 후 제거")
    @Test
    void removeChildCategory() {
        // when
        parentCategory.addChildCategory(childCategory);
        parentCategory.removeChildCategory(childCategory);

        // then
        assertThat(parentCategory.getChildCategoryList().contains(childCategory)).isFalse();
        assertThat(childCategory.getParentCategory()).isEqualTo(null);
    }
    
    @DisplayName("존재하지 않는 하위 카테고리 제거 시도")
    @Test
    void removeNotExistingChildCategory() {
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parentCategory.removeChildCategory(childCategory));

        // then
        assertEquals(exception.getMessage(), "존재하지 않는 하위 카테고리입니다.");
    }
}
