package com.around.tdd.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MailDto {

    private String from;
    private String to;
    private String title;
    private String content;

}
