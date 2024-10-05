package com.around.tdd.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerSeq;

    private String phone;

    private String name;

    private String address;

    private String detailAddress;

    private String post;

    private Integer salesTotalAmount;

    private Integer deliveryTotalAmount;

    @Builder
    public Seller(String phone, String name, String address, String detailAddress, String post, Integer salesTotalAmount, Integer deliveryTotalAmount) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.detailAddress = detailAddress;
        this.post = post;
        this.salesTotalAmount = salesTotalAmount;
        this.deliveryTotalAmount = deliveryTotalAmount;
    }

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    @JsonIgnore
    private Member member;

}