package com.around.tdd.service;

import com.around.tdd.vo.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailDto.getFrom());
        message.setTo(mailDto.getTo());
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getContent());
        javaMailSender.send(message);
    }


}
