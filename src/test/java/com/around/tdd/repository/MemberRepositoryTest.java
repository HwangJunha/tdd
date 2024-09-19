package com.around.tdd.repository;

import com.around.tdd.vo.*;
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

    @Autowired
    private MemberAuthDictionaryRepository memberAuthDictionaryRepository;

    @Nested
    class MemberFindTest{
        private Member member;
        @BeforeEach
        void setUp(){

            this.member = Member
                    .builder()
                    .id("tarot1415")
                    .password("!!1q2w3e4r")
                    .state(1)
                    .build();
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

            MemberDeliveryInfo memberDeliveryInfo2 = MemberDeliveryInfo
                    .builder()
                    .name("황준하2")
                    .phone("01084282511")
                    .email("tarot10@naver.com")
                    .nick("tarot1415")
                    .address("서울특별시 관악구 신림동 1415-10")
                    .detailAddress("아덴빌 204호")
                    .post("08755")
                    .build();

            MemberDeliveryInfo memberDeliveryInfo3 = MemberDeliveryInfo
                    .builder()
                    .name("황준하3")
                    .phone("01084282511")
                    .email("tarot10@naver.com")
                    .nick("tarot1415")
                    .address("서울특별시 관악구 신림동 1415-10")
                    .detailAddress("아덴빌 204호")
                    .post("08755")
                    .build();

            this.member.setMemberDeliveryInfo(List.of(memberDeliveryInfo, memberDeliveryInfo2, memberDeliveryInfo3));
            Member savedMember = memberRepository.save(member);
            List<Member> resultMember = memberRepository.findAll();
            assertThat(resultMember.get(0).getMemberDeliveryInfo().size()).isEqualTo(3);
            assertThat(resultMember.get(0)).isEqualTo(member);
        }

        @DisplayName("회원권한 추가 테스트")
        @Test
        void memberAuthInsertTest(){
            var savedMember = memberRepository.save(member);

            MemberAuthDictionary memberAuthDictionary1 = MemberAuthDictionary
                    .builder()
                    .authName("임시권한1")
                    .build();

            MemberAuthDictionary memberAuthDictionary2 = MemberAuthDictionary
                    .builder()
                    .authName("임시권한2")
                    .build();

            var savedMemberAuthDictionary1 = memberAuthDictionaryRepository.save(memberAuthDictionary1);
            var savedMemberAuthDictionary2 = memberAuthDictionaryRepository.save(memberAuthDictionary2);

            var memberAuth1 = new MemberAuth();
            memberAuth1.setMemberAuthId(new MemberAuthId(member.getMemberSeq(), savedMemberAuthDictionary1.getMemberAuthDictionarySeq()));

            memberAuth1.setMember(member);
            memberAuth1.setMemberAuthDictionary(memberAuthDictionary1);

            var memberAuth2 = new MemberAuth();
            memberAuth2.setMemberAuthId(new MemberAuthId(member.getMemberSeq(), savedMemberAuthDictionary2.getMemberAuthDictionarySeq()));

            memberAuth2.setMember(member);
            memberAuth2.setMemberAuthDictionary(memberAuthDictionary2);

            savedMember.setMemberAuthList(List.of(memberAuth1, memberAuth2));

            var listMember = memberRepository.findAll();
            assertThat(listMember.size()).isEqualTo(1);
            assertThat(listMember).contains(member);

            assertThat(listMember.get(0).getMemberAuthList().size()).isEqualTo(2);
            assertThat(listMember.get(0).getMemberAuthList()).contains(memberAuth1);
            assertThat(listMember.get(0).getMemberAuthList()).contains(memberAuth2);

        }
    }





}