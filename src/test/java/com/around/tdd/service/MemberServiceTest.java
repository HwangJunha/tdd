package com.around.tdd.service;

import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("사용자 정보 조회")
    void memberFindByIdTest(){
        Long memberSeq = 1L;
        String id = "junha1";
        String password = "!!1q2w3e4r";
        Integer state = 1;
        Member member = new Member();
        member.setMemberSeq(memberSeq);
        member.setId(id);
        member.setPassword(password);
        member.setState(state);
        when(memberRepository.findById(memberSeq)).thenReturn(Optional.of(member));

        Optional<Member> resultMember = memberService.memberFindById(memberSeq);

        assertThat(resultMember.isPresent()).isTrue();
        assertThat(resultMember.get().getId()).isEqualTo(id);
    }
}