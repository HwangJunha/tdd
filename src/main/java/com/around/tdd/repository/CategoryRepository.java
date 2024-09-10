package com.around.tdd.repository;

import com.around.tdd.vo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
    boolean existsByName(String name);
}
