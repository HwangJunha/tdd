package com.around.tdd.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShippingDTO {
    private Long orderSeq;
    private String address;
    private String detailAddress;
    private String post;
    private String phone;
}
