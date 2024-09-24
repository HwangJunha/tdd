package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq")
    private Integer commentSeq;

    @ManyToOne
    @JoinColumn(name = "board_seq", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "member_seq", nullable = false)
    private Member member;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "input_dt")
    private LocalDateTime inputDt;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
