package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Category {

    @GeneratedValue
    @Id
    private Long categorySeq;

    private String name;

    private Integer depth;

    private Integer sort;

    @Column(columnDefinition = "enum('y', 'n') default 'y'")
    private Character displayYn;

    @CreationTimestamp // insert 시 현재 시각 입력
    @Column(name = "create_dt")
    private LocalDateTime createDateTime;

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
