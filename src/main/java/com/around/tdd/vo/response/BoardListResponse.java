package com.around.tdd.vo.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardListResponse {
    private Long boardSeq;
    private  String memberId;
    private String title;
    private int views;
    private LocalDateTime inputDt;
}
