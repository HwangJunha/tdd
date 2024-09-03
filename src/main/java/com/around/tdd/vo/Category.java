package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Category> childCategoryList;

    // 부모 카테고리 연관관계 설정
    public void linkParentCategory(Category parentCategory) {
        parentCategory.setParentCategory(parentCategory);
        parentCategory.childCategoryList.add(this);
    }
}
