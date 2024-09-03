package com.around.tdd.service;

import com.around.tdd.vo.MailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSendServiceTest {

    @InjectMocks
    private EmailSendService emailSendService;

    @Mock
    private JavaMailSender javaMailSender;

    @ParameterizedTest
    @MethodSource("emailDtoProvider")
    @DisplayName("이메일 발송 테스트")
    void sendSimpleMessageTest(MailDto mailDto) {
        emailSendService.sendSimpleMessage(mailDto);
        verify(javaMailSender, atLeastOnce()).send(any(SimpleMailMessage.class)); // AuthService의 메서드 호출 검증
    }

    private static Stream<MailDto> emailDtoProvider() {
        return Stream.of(
                new MailDto("tarot1415@gmail.com", "tarot10@naver.com", "인증번호", "인증번호: 999999")
        );
    }
}