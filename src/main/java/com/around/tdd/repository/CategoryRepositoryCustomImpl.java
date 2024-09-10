package com.around.tdd.repository;

import com.around.tdd.vo.Category;
import com.around.tdd.vo.CategorySearchRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.around.tdd.vo.QCategory.category;

public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CategoryRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Category> findCategoryList(CategorySearchRequest searchRequest) {
        // 조회 조건 및 정렬 지정
        return queryFactory
                .selectFrom(category)
                .where(
                        searchRequest.getName() != null ? category.name.containsIgnoreCase(searchRequest.getName()) : null,
                        searchRequest.getDepth() != null ? category.depth.eq(searchRequest.getDepth()) : null,
                        searchRequest.getDisplayYn() != null ? category.displayYn.eq(searchRequest.getDisplayYn()) : null,
                        searchRequest.getParentCategorySeq() != null ? category.parentCategory.categorySeq.eq(searchRequest.getParentCategorySeq()) : null
                )
                .orderBy(category.sort.asc())
                .fetch();
    }
}
