package com.around.tdd.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    private Long cartSeq;
    private int productSeq;
    private int productNum;
}
