package com.around.tdd.vo.request;

public record MemberRequest(Long memberSeq, String id, String password, Integer state) {
}
