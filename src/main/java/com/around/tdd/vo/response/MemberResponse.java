package com.around.tdd.vo.response;

import com.around.tdd.vo.Member;

public record MemberResponse(
        Long memberSeq,
        String id,
        String password,
        Integer state) {

    public MemberResponse(Member member){
        this(member.getMemberSeq(), member.getId(), member.getPassword(), member.getState());
    }
}
