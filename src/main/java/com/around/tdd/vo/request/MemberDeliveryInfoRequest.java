package com.around.tdd.vo.request;

public record MemberDeliveryInfoRequest(
    Long memberDeliverySeq,
    String name,
    String phone,
    String email,
    String nick,
    String address,
    String detailAddress,
    String post
) {

}
