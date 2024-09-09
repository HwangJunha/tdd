package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "board_content")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardContent {

    @Id
    @Column(name = "board_seq")
    private Long boardSeq;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "board_seq", nullable = false)
    private Board board;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
}
