package com.around.tdd.vo.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardDetailResponse {
    private Long boardSeq;
    private  String memberId;
    private String title;
    private String content;
    private int views;
    private LocalDateTime inputDt;
}
