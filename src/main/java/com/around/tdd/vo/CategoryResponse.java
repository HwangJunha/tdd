package com.around.tdd.vo;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CategoryResponse {
    private Long categorySeq;

    private String name;

    private Integer depth;

    private Integer sort;

    private Character displayYn;

    private List<CategoryResponse> childCategoryList;
}
