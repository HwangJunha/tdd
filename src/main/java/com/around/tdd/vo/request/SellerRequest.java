package com.around.tdd.vo.request;

import com.around.tdd.vo.Seller;

public record SellerRequest(
        Long sellerSeq,
        Long memberSeq,
        String phone,
        String name,
        String address,
        String detailAddress,
        String post,
        Integer salesTotalAmount,
        Integer deliveryTotalAmount
) {

    public Seller toSeller(){
        return Seller.builder()
                .phone(phone)
                .name(name)
                .address(address)
                .detailAddress(detailAddress)
                .post(post)
                .salesTotalAmount(salesTotalAmount)
                .deliveryTotalAmount(deliveryTotalAmount)
                .build();
    }
}
