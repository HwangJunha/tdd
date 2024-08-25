package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "board_content")
public class BoardContent {

    @Id
    @Column(name = "board_seq")
    private Integer boardSeq;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId // MapsId 는 @id로 지정한 컬럼에 @OneToOne 이나 @ManyToOne 관계를 매핑시키는 역할
    @JoinColumn(name = "board_seq")
    private Board board;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
}
