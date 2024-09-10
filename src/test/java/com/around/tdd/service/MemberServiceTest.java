package com.around.tdd.service;

import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberDeliveryInfo;
import com.around.tdd.vo.MemberInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;


    @Nested
    class MemberFindByTest{
        Member member;
        Long memberSeq = 1L;
        String id = "junha1";
        @BeforeEach
        void setUp() {
            String password = "!!1q2w3e4r";
            Integer state = 1;
            this.member = Member.builder()
                    .state(state)
                    .id(id)
                    .password(password)
                    .build();
        }

        @Test
        @DisplayName("사용자 정보 조회")
        void memberFindByIdTest() {
            when(memberRepository.findById(memberSeq)).thenReturn(Optional.of(member));
            Optional<Member> resultMember = memberService.memberFindById(memberSeq);
            assertThat(resultMember.isPresent()).isTrue();
            assertThat(resultMember.get().getId()).isEqualTo(id);
        }
    }

    @Nested
    class CheckPassword{
        private String failPassword;
        private String successPassword;
        @BeforeEach
        void setUp(){
            failPassword = "123456";
            successPassword = "!!tjdgk7518";
        }
        @DisplayName("패스워드 유효성을 체크한다")
        @Test
        void checkPasswordTest() {
            boolean isFail = memberService.checkPassword(failPassword);
            boolean isSuccess = memberService.checkPassword(successPassword);
            assertThat(isFail).isFalse();
            assertThat(isSuccess).isTrue();
        }
    }

    @Nested
    class CheckEmail{
        private String failEmail;
        private String successEmail;
        private String sizeOverEmail;
        private String sizeInEmail;

        @BeforeEach
        void setUp(){
            this.failEmail = "qweqweqweqwe";
            this.successEmail = "taort1415@gmail.com";
            this.sizeOverEmail = "wwwwwwqqqqqqqqqqqqqqq@gmail.com"; //31자
            this.sizeInEmail = "wwwwwqqqqqqqqqqqqqqq@gmail.com"; //30자
        }

        @DisplayName("이메일 유효성을 체크한다")
        @Test
        void checkEmailTest(){
            assertThat(memberService.checkEmail(failEmail)).isFalse();
            assertThat(memberService.checkEmail(successEmail)).isTrue();
            assertThat(memberService.checkEmail(sizeOverEmail)).isFalse();
            assertThat(memberService.checkEmail(sizeInEmail)).isTrue();

        }
    }

    @Nested
    class CheckId{
        private String duplicateId;
        private String successId;

        @BeforeEach
        void setUp(){
            this.duplicateId = "junha1234";
            this.successId = "tarot1415";
        }

        @DisplayName("아이디가 중복되었는지 체크한다")
        @Test
        void checkIdTest(){
            when(memberRepository.countById(this.duplicateId)).thenReturn(1L);
            when(memberRepository.countById(eq(this.successId))).thenReturn(0L);

            assertThat(memberService.checkId(this.duplicateId)).isFalse();
            assertThat(memberService.checkId(this.successId)).isTrue();
        }
    }

    @Nested
    class InsertMember{
        private Member member;
        private MemberInfo memberInfo;
        private List<MemberDeliveryInfo> memberDeliveryInfoList;


        @BeforeEach
        void setUp(){
            this.memberInfo = new MemberInfo();
            this.memberInfo.setSocialNumber("9603291234567");
            this.memberInfo.setName("junha");
            this.memberInfo.setPhone("01084282511");
            this.memberInfo.setEmail("tarot1415@gmail.com");
            this.memberInfo.setNick("tarot1415");
            this.memberInfo.setGender("M");
            this.memberInfo.setBirth(LocalDateTime.of(1996, 03, 29, 0, 0));
            this.memberInfo.setAddress("서울시 관악구 신림동 1415-10");
            this.memberInfo.setDetailAddress("아덴빌 104호");
            this.memberInfo.setPost("088450");
            MemberDeliveryInfo memberDeliveryInfo1 = MemberDeliveryInfo
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

            member = Member
                    .builder()
                    .id("tarot1415")
                    .password("!!1q2w3e4r")
                    .state(1)
                    .build();
            memberDeliveryInfoList = List.of(memberDeliveryInfo1, memberDeliveryInfo2);
            member.setMemberInfo(this.memberInfo);
            member.setMemberDeliveryInfo(memberDeliveryInfoList);
        }

        @Test
        @DisplayName("insert memberInfo 테스트")
        void insertMemberInfoTest(){
            when(memberRepository.save(any(Member.class))).thenReturn(member);
            Member savedMember = memberService.insertMemberInfo(member);
            assertNotNull(savedMember);
            assertEquals("tarot1415", savedMember.getId());
            assertEquals("!!1q2w3e4r", savedMember.getPassword());
            assertNotNull(savedMember.getMemberInfo());
            assertEquals("junha", savedMember.getMemberInfo().getName());
            verify(memberRepository, times(1)).save(member);
        }

        @Test
        @DisplayName("insert memberDeliveryInfo 테스트")
        void insertMemberDeliveryInfoTest(){
            when(memberRepository.save(any(Member.class))).thenReturn(member);
            Member savedMember = memberService.insertMemberInfo(member);
            assertNotNull(savedMember);
            assertEquals("tarot1415", savedMember.getId());
            assertEquals("!!1q2w3e4r", savedMember.getPassword());
            assertNotNull(savedMember.getMemberDeliveryInfo());
            assertEquals("황준하", savedMember.getMemberDeliveryInfo().getFirst().getName());
            assertThat(savedMember.getMemberDeliveryInfo().size()).isEqualTo(2);
            verify(memberRepository, times(1)).save(member);
        }
    }
}