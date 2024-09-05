package com.around.tdd.service;

import com.around.tdd.exception.DuplicateCategoryException;
import com.around.tdd.repository.CategoryRepository;
import com.around.tdd.vo.Category;
import com.around.tdd.vo.CategorySaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long saveCategory(CategorySaveRequest categoryRequest) {

        // 카테고리 중복 검증
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new DuplicateCategoryException("중복 카테고리 : " + categoryRequest.getName());
        }

        // DTO 엔티티 변환
        Category category = categoryRequest.toEntity();

        // 부모 카테고리 존재 시 연관관계 설정
        if (categoryRequest.getParentCategorySeq() != null && categoryRequest.getParentCategorySeq() > 0) {
            Optional<Category> parentCategory = categoryRepository.findById(categoryRequest.getParentCategorySeq());
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
}
