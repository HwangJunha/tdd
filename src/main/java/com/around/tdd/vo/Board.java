package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "Board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_seq", nullable = false)
    private Integer boardSeq;

    @ManyToOne
    @JoinColumn(name = "member_seq", nullable = false)
    private Member member;

    @Column(name = "title")
    private String title;

    @Column(name = "input_dt")
    private LocalDateTime inputDt;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "views")
    private int views;

}
