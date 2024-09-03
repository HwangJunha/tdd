package com.around.tdd.vo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private Long memberSeq;
    private String authNumber;
    private String authToken;
}
