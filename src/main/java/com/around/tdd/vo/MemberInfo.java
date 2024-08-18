package com.around.tdd.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class MemberInfo {

    @Id
    @GeneratedValue
    private Integer memberSeq;

    @OneToOne(mappedBy = "memberInfo")
    private Member member;

    private String socialNumber;

    private String name;

    private String phone;

    private String email;

    private String nick;

    private String gender;

    private LocalDateTime birth;

    private String address;

    private String detailAddress;

    private String post;
}
