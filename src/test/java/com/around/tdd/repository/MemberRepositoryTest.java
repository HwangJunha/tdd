package com.around.tdd.repository;

import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberDeliveryInfo;
import com.around.tdd.vo.MemberInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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


    @Nested
    class MemberFindTest{
        private Member member;
        @BeforeEach
        void setUp(){
            this.member = new Member("junha1", "!!1q2w3e4r", 1);

        }

        @DisplayName("회원 입력 및 조회 테스트")
        @Test
        void memberInfoFindTest(){
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
            this.member.setMemberInfo(memberInfo);
            Member savedMember = memberRepository.save(member);
            List<Member> resultMember = memberRepository.findAll();
            assertThat(resultMember.get(0)).isEqualTo(member);
        }

        @DisplayName("회원 배송지 입력 및 조회 테스트")
        @Test
        void memberDeliveryInfoTest(){
            MemberDeliveryInfo memberDeliveryInfo = MemberDeliveryInfo
                    .builder()
                    .name("황준하")
                    .phone("01084282511")
                    .email("tarot10@naver.com")
                    .nick("tarot1415")
                    .address("서울특별시 관악구 신림동 1415-10")
                    .detailAddress("아덴빌 204호")
                    .post("08755")
                    .build();

            this.member.setMemberDeliveryInfo(List.of(memberDeliveryInfo));
            List<Member> resultMember = memberRepository.findAll();
            Member savedMember = memberRepository.save(member);
            System.out.println("savedMember:"+savedMember.toString());

        }

    }





}