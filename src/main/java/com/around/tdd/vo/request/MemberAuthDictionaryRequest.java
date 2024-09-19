package com.around.tdd.vo.request;

import com.around.tdd.vo.MemberAuthDictionary;

public record MemberAuthDictionaryRequest(Long memberAuthDictionarySeq, String authName) {

    public MemberAuthDictionary fromMemberAuthDictionary() {
        return MemberAuthDictionary.builder()
                .authName(authName)
                .build();
    }

}
