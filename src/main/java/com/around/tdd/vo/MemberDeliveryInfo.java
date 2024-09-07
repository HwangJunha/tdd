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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberDeliverySeq;
    private String name;
    private String phone;
    private String email;
    private String nick;
    private String address;
    private String detailAddress;
    private String post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;

    @Builder
    public MemberDeliveryInfo(String name, String phone, String email, String nick, String address, String detailAddress, String post){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nick = nick;
        this.address = address;
        this.detailAddress = detailAddress;
        this.post = post;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
