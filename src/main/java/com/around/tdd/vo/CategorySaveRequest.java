package com.around.tdd.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorySaveRequest {
    private Long categorySeq;

    @NotBlank(message = "카테고리명 미입력")
    @Size(message = "카테고리명 1 ~ 100자 이내", min = 1, max = 100)
    private String name;

    @NotNull(message = "카테고리 깊이 미입력")
    private Integer depth;

    private Character displayYn;

    @NotNull(message = "정렬 순서 미입력")
    private Integer sort;

    private Long parentCategorySeq;

    // 엔티티 변환
    public Category toEntity() {
        return Category.builder()
                        .categorySeq(categorySeq)
                        .name(name)
                        .depth(depth)
                        .displayYn(displayYn)
                        .sort(sort)
                        .build();
    }
}
