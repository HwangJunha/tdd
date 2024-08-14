package com.around.tdd.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@Getter
public class Cart {
    @Id
    @GeneratedValue
    private Long cartSeq;

    private Long memberSeq;

    private Long productSeq;

    private Integer productNum;
}
