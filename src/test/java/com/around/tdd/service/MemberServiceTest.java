package com.around.tdd.service;

import com.around.tdd.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

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



}
