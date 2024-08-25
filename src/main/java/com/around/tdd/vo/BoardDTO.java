package com.around.tdd.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class BoardDTO {
    private  Long memberSeq;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private String imageUrl;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;
}
