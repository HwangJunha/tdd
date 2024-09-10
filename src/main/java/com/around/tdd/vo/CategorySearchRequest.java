package com.around.tdd.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorySearchRequest {
    private String name;
    private Integer depth;
    private Character displayYn;
    private Long parentCategorySeq;
}
