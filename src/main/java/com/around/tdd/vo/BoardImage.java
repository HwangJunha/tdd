package com.around.tdd.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "board_image")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_image_seq")
    private Long boardImageSeq;

    @ManyToOne
    @JoinColumn(name = "board_seq", nullable = false)
    @JsonIgnore
    private Board board;

    @Column(name = "save_name")
    private String saveName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "input_dt")
    private LocalDateTime inputDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;
}
