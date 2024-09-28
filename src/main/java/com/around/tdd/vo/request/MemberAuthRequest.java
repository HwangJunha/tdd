package com.around.tdd.vo.request;

import com.around.tdd.vo.MemberAuth;
import com.around.tdd.vo.MemberAuthId;

public record MemberAuthRequest(Long memberSeq, Long memberAuthDictionarySeq){

    public MemberAuth fromMemberAuth(){
        var memberAuth = new MemberAuth();
        var memberAuthId = new MemberAuthId(memberSeq, memberAuthDictionarySeq);
        memberAuth.setMemberAuthId(memberAuthId);
        return memberAuth;
    }
}
