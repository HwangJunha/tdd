package com.around.tdd.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
public class BoardRequest {
    private Long boardSeq;
    private  String memberId;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

}