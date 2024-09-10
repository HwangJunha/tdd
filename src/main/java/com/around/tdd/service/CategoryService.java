package com.around.tdd.service;

import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.repository.CategoryRepository;
import com.around.tdd.vo.Category;
import com.around.tdd.vo.CategoryResponse;
import com.around.tdd.vo.CategorySaveRequest;
import com.around.tdd.vo.CategorySearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 저장
     * @param categorySaveRequest
     * @return
     */
    @Transactional
    public Long saveCategory(CategorySaveRequest categorySaveRequest) {

        // 카테고리 중복 검증
        if (categoryRepository.existsByName(categorySaveRequest.getName())) {
            throw new DuplicateCategoryException("중복 카테고리 : " + categorySaveRequest.getName());
        }

        // DTO 엔티티 변환
        Category category = categorySaveRequest.toEntity();

        // 부모 카테고리 존재 시 연관관계 설정
        if (categorySaveRequest.getParentCategorySeq() != null && categorySaveRequest.getParentCategorySeq() > 0) {
            Optional<Category> parentCategory = categoryRepository.findById(categorySaveRequest.getParentCategorySeq());
            parentCategory.ifPresent(category::linkParentCategory);
        }

        // 카테고리 저장
        try {
            Category savedCategory = categoryRepository.save(category);
            return savedCategory.getCategorySeq();
        } catch (Exception e) {
            throw new RuntimeException("카테고리 저장 실패");
        }
    }

    /**
     * 카테고리 목록 조회
     * @param searchRequest
     * @return
     */
    public List<CategoryResponse> findCategoryList(CategorySearchRequest searchRequest) {
        List<Category> categoryList = categoryRepository.findCategoryList(searchRequest);

        return categoryList.stream()
                            .map(this::convertToDto)
                            .toList();
    }

    /**
     * 카테고리 단일 조회
     * @param categorySeq
     * @return
     */
    public CategoryResponse findCategory(Long categorySeq) {
        Category category = categoryRepository.findById(categorySeq)
                                .orElseThrow(() -> new RuntimeException("카테고리가 존재하지 않습니다."));
        return convertToDto(category);
    }

    /**
     * 카테고리 엔티티 DTO로 변환
     * @param category 카테고리 엔티티
     * @return 카테고리 DTO
     */
    private CategoryResponse convertToDto(Category category) {
        CategoryResponse categoryDto = new CategoryResponse();
        categoryDto.setName(category.getName());
        categoryDto.setCategorySeq(category.getCategorySeq());
        categoryDto.setSort(category.getSort());
        categoryDto.setDepth(category.getDepth());
        categoryDto.setDisplayYn(category.getDisplayYn());
        categoryDto.setChildCategoryList(category.getChildCategoryList().stream()
                .map(this::convertToDto)
                .toList());

        return categoryDto;
    }
}
