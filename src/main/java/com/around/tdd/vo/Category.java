package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Category {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long categorySeq;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer depth;

    @Column(nullable = false)
    private Integer sort;

    @Column(nullable = false, columnDefinition = "enum('y', 'n') default 'y'")
    private Character displayYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_seq")
    private Category parentCategory;

    @Builder.Default
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Category> childCategoryList = new ArrayList<>();

    // 자식 카테고리 추가
    public void addChildCategory(Category childCategory) {
        childCategoryList.add(childCategory);
        childCategory.setParentCategory(this);
    }

    // 자식 카테고리 제거
    public void removeChildCategory(Category childCategory) {
        childCategoryList.remove(childCategory);
        childCategory.setParentCategory(null);
    }
}
