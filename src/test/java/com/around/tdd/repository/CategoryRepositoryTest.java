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
    private Category testParentCategory;
    private Category testChildCategory;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        // given
        Category categoryEntity1 = createCategoryEntity("카테고리1", 1, 1, 'y');
        Category categoryEntity2 = createCategoryEntity("카테고리2", 2, 2, 'y');
        categoryEntity1.addChildCategory(categoryEntity2);

        testParentCategory = categoryRepository.save(categoryEntity1);
        testChildCategory = categoryRepository.save(categoryEntity2);
    }

    @DisplayName("카테고리 단일 조회")
    @Test
    void findOneCategory() {
        // when
        Optional<Category> category = categoryRepository.findById(testParentCategory.getCategorySeq());

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
        assertThat(categoryList).contains(testParentCategory, testChildCategory);
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
        categorySearchRequest.setDepth(testParentCategory.getDepth());
        categorySearchRequest.setDisplayYn(testParentCategory.getDisplayYn());

        // when
        List<Category> categoryList = categoryRepository.findCategoryList(categorySearchRequest);

        // then
        assertThat(categoryList).isNotNull();
        assertThat(categoryList).isNotEmpty();
        assertThat(categoryList.size()).isEqualTo(1);
        assertThat(categoryList).contains(testParentCategory);
    }
    
    @DisplayName("상위 카테고리 제거 시 하위 카테고리 동시 제거")
    @Test
    void deleteParentCategoryWithChildren() {
        // when
        categoryRepository.deleteById(testParentCategory.getCategorySeq());

        // then
        assertThat(categoryRepository.findAll()).isNotNull();
        assertThat(categoryRepository.findAll()).isEmpty();
        assertThat(categoryRepository.findById(testParentCategory.getCategorySeq()).isPresent()).isFalse();
    }

    @DisplayName("하위 카테고리 제거")
    @Test
    void deleteChildCategory() {
        // when
        testParentCategory.removeChildCategory(testChildCategory);
        categoryRepository.deleteById(testChildCategory.getCategorySeq());

        // then
        assertThat(categoryRepository.findAll()).isNotNull();
        assertThat(categoryRepository.findAll().size()).isEqualTo(1);
        assertThat(categoryRepository.findAll()).contains(testParentCategory);
        assertThat(categoryRepository.findById(testChildCategory.getCategorySeq()).isPresent()).isFalse();
    }

    @DisplayName("카테고리 전체 제거 성공")
    @Test
    void deleteAllCategorySuccess() {
        // when
        categoryRepository.deleteAll();

        // then
        assertThat(categoryRepository.findAll()).isNotNull();
        assertThat(categoryRepository.findAll()).isEmpty();
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
