package com.around.tdd.repository;

import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원 정보 조회 테스트")
    @Test
    void memberInfoFindTest(){
        Member member = new Member();
        member.setMemberSeq(1L);
        member.setId("junha1");
        member.setPassword("!!q1w2e3r4");
        member.setState(1);
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setSocialNumber("9603291111111");
        memberInfo.setName("황준하");
        memberInfo.setEmail("tarot10@naver.com");
        memberInfo.setPhone("01084282511");
        memberInfo.setNick("taro1");
        memberInfo.setGender("M");
        memberInfo.setAddress("서울특별시 관악구 신림동 1415-10");
        memberInfo.setDetailAddress("아덴빌 204호");
        memberInfo.setPost("08755");
        memberInfo.setBirth(LocalDateTime.of(1996, 03, 29, 0, 0, 0));
        member.setMemberInfo(memberInfo);
        Member savedMember = memberRepository.save(member);
        List<Member> resultMember = memberRepository.findAll();

        assertThat(resultMember.get(0)).isEqualTo(member);
    }

}