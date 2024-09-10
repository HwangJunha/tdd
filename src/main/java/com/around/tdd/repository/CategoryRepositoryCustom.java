package com.around.tdd.repository;

import com.around.tdd.vo.Category;
import com.around.tdd.vo.CategorySearchRequest;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> findCategoryList(CategorySearchRequest searchRequest);
}
