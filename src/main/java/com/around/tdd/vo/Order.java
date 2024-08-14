package com.around.tdd.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@RequiredArgsConstructor
@Getter
public class Order {
    @Id
    @GeneratedValue
    private Long orderSeq;

    private Long memberSeq;

    private String orderName;

    private String orderPhone;

    private String orderAddress;

    private String post;

    private Date orderDateTime;

    private Integer orderState;

    private List<OrderDetail> orderDetailList;
}
