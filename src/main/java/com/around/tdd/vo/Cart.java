package com.around.tdd.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Cart {
    @Id
    @Column(name = "cart_seq", nullable = false)
    private Long cartSeq;

    @Column(name ="member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "product_seq", nullable = false)
    private Long productSeq;

    @Column(name="product_num", nullable = false)
    private Integer productNum;
}
