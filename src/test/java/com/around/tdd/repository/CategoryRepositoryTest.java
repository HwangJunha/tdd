package com.around.tdd.repository;

import com.around.tdd.vo.Category;
import com.around.tdd.vo.CategorySearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    // 클래스 내에서 테스트를 위한 카테고리 엔티티 필드
    private Category testCategory1;
    private Category testCategory2;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        // given
        Category categoryEntity1 = createCategoryEntity("카테고리1", 1, 1, 'y');
        Category categoryEntity2 = createCategoryEntity("카테고리2", 2, 2, 'y');
        categoryEntity2.linkParentCategory(categoryEntity1);

        testCategory1 = categoryRepository.save(categoryEntity1);
        testCategory2 = categoryRepository.save(categoryEntity2);
    }

    @DisplayName("카테고리 단일 조회")
    @Test
    void findOneCategory() {
        // when
        Optional<Category> category = categoryRepository.findById(testCategory1.getCategorySeq());

        // then
        assertThat(category.isPresent()).isTrue();
        assertThat(category.get().getSort()).isEqualTo(1);
        assertThat(category.get().getDepth()).isEqualTo(1);
        assertThat(category.get().getName()).isEqualTo("카테고리1");
        assertThat(category.get().getDisplayYn()).isEqualTo('y');
    }

    @DisplayName("카테고리 전체 목록 조회")
    @Test
    void findCategoryList() {
        // when
        List<Category> categoryList = categoryRepository.findAll();

        // then
        assertThat(categoryList).isNotNull();
        assertThat(categoryList).isNotEmpty();
        assertThat(categoryList.size()).isEqualTo(2);
        assertThat(categoryList).contains(testCategory1, testCategory2);
    }

    @DisplayName("카테고리 전체 목록 조회 - Custom 쿼리 사용")
    @Test
    void findCategoryListWithCustomQuery() {
        // given
        CategorySearchRequest categorySearchRequest = new CategorySearchRequest();

        // when
        List<Category> categoryList = categoryRepository.findCategoryList(categorySearchRequest);

        // then
        assertThat(categoryList).isNotNull();
        assertThat(categoryList).isNotEmpty();
        assertThat(categoryList.size()).isEqualTo(2);
    }

    @DisplayName("카테고리 목록 조건 조회")
    @Test
    void findCategoryListByCondition() {
        // given
        CategorySearchRequest categorySearchRequest = new CategorySearchRequest();
        categorySearchRequest.setDepth(testCategory1.getDepth());
        categorySearchRequest.setDisplayYn(testCategory1.getDisplayYn());

        // when
        List<Category> categoryList = categoryRepository.findCategoryList(categorySearchRequest);

        // then
        assertThat(categoryList).isNotNull();
        assertThat(categoryList).isNotEmpty();
        assertThat(categoryList.size()).isEqualTo(1);
        assertThat(categoryList).contains(testCategory1);
    }

    // 카테고리 엔티티 생성
    private Category createCategoryEntity(String name, Integer sort, Integer depth, Character displayYn) {
        return Category.builder()
                .name(name)
                .sort(sort)
                .depth(depth)
                .displayYn(displayYn)
                .build();
    }
}