package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "board_image")
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_image_seq")
    private Integer boardImageSeq;

    @ManyToOne
    @JoinColumn(name = "board_seq", nullable = false)
    private Board board;

    @Column(name = "save_name")
    private String saveName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

}
