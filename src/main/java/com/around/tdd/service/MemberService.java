package com.around.tdd.service;

import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean checkPassword(String password){
        String regExp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";
        return password.matches(regExp);
    }

    public boolean checkEmail(String email){
        String regExp = "^(?=.{1,30}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regExp);
    }

    /**
     * 같은 아이디가 있는지 체크
     * @param id - 검사할 ID
     * @return
     */
    public boolean checkId(String id){
        return memberRepository.countById(id).intValue() > 0 ? false : true;
    }

    public Member insertMemberInfo(Member member){
        return memberRepository.save(member);
    }

    /**
     * 사용자 정보를 조회한다
     * @param memberSeq - 사용자 번호
     * @return 사용자 정보를 반환한다
     */
    public Optional<Member> memberFindById(Long memberSeq){
        return memberRepository.findById(memberSeq);
    }


}
