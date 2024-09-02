package com.around.tdd.service;

import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 사용자 정보를 조회한다
     * @param memberSeq - 사용자 번호
     * @return 사용자 정보를 반환한다
     */
    public Optional<Member> memberFindById(Long memberSeq){
        return memberRepository.findById(memberSeq);
    }
}
