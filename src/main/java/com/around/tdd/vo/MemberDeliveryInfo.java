package com.around.tdd.vo;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDeliveryInfo {

    @Id
    @GeneratedValue
    private Long memberDeliverySeq;
    private String name;
    private String phone;
    private String email;
    private String nick;
    private String address;
    private String detailAddress;
    private String post;

    @JoinColumn
    private Long memberSeq;

    @Builder
    public MemberDeliveryInfo(String name, String phone, String email, String nick, String address, String detailAddress, String post, Long memberSeq){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nick = nick;
        this.address = address;
        this.detailAddress = detailAddress;
        this.post = post;
        this.memberSeq = memberSeq;
    }

}
