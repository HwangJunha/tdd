package com.around.tdd.repository;

import com.around.tdd.vo.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @DisplayName("카테고리 저장 성공")
    @Test
    @Rollback(value = false) // insert 로그 확인 위함
    void saveCategorySuccess() {
        // given
        String name = "테스트 카테고리";
        int sort = 1;
        int depth = 1;
        char displayYn = 'y';

        Category category = Category.builder()
                                    .name(name)
                                    .sort(sort)
                                    .depth(depth)
                                    .displayYn(displayYn)
                                    .build();

        // when
        Category savedCategory = categoryRepository.save(category);
        Optional<Category> findCategory = categoryRepository.findById(savedCategory.getCategorySeq());

        // then
        assertThat(findCategory.isPresent()).isTrue();
        assertThat(savedCategory.getName()).isEqualTo(name);
        assertThat(savedCategory.getSort()).isEqualTo(sort);
        assertThat(savedCategory.getDepth()).isEqualTo(depth);
    }
}