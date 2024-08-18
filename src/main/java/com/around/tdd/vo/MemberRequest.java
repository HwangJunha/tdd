package com.around.tdd.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    private Long memberSeq;
    private String authNumber;
    private String authToken;
}
