package com.around.tdd.vo;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@Getter
public class OrderDetail {
    @Id
    @GeneratedValue
    private Long orderDetailSeq;

    private Long productSeq;

    private Integer price;

    private Integer count;

}
