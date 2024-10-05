package com.around.tdd.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MemberInfo {

    @Id
    private Long memberSeq;


    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    @JsonIgnore
    private Member member;

    @NotNull
    private String socialNumber;
    @NotNull
    private String name;
    @NotNull
    private String phone;
    @Email
    private String email;
    @NotNull
    private String nick;
    @NotNull
    private String gender;
    @NotNull
    private LocalDateTime birth;
    @NotNull
    private String address;
    @NotNull
    private String detailAddress;
    @NotNull
    private String post;

    @Builder
    public MemberInfo(String socialNumber, String name, String phone, String email, String nick, String gender, LocalDateTime birth, String address, String detailAddress, String post) {
        this.socialNumber = socialNumber;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nick = nick;
        this.gender = gender;
        this.birth = birth;
        this.address = address;
        this.detailAddress = detailAddress;
        this.post = post;
    }
}
