package com.around.tdd.vo.request;

import com.around.tdd.vo.AuthType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private Long memberSeq;
    private String authNumber;
    private String authToken;
    private AuthType authType;
}
