package com.around.tdd.vo.request;

import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberDeliveryInfo;
import com.around.tdd.vo.MemberInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record MemberRequest(
        Long memberSeq,
        String id,
        String password,
        String socialNumber,
        String name,
        String phone,
        String email,
        String nick,
        String gender,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime birth,
        String address,
        String detailAddress,
        String post,
        List<MemberDeliveryInfoRequest> memberDeliveryInfoRequestList) {



    public Member fromMember() {
        return Member.builder()
                .id(this.id)
                .password(this.password)
                .state(1)
                .build();
    }

    public MemberInfo fromMemberInfo(){
        return MemberInfo.builder()
                .socialNumber(this.socialNumber())
                .name(this.name())
                .phone(this.phone())
                .email(this.email())
                .nick(this.nick())
                .gender(this.gender())
                .birth(this.birth())
                .address(this.address())
                .detailAddress(this.detailAddress())
                .post(this.post())
                .build();
    }

    public List<MemberDeliveryInfo> fromMemberDeliveryInfo(){
        return this.memberDeliveryInfoRequestList
                .stream()
                .map(request -> {
                    return MemberDeliveryInfo.builder()
                            .name(request.name())
                            .phone(request.phone())
                            .email(request.email())
                            .nick(request.nick())
                            .address(request.address())
                            .detailAddress(request.detailAddress())
                            .post(request.post())
                            .build();
                })
                .collect(Collectors.toList());

    }
}
