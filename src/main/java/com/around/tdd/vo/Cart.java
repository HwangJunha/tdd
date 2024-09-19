package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @Column(name = "cart_seq", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartSeq;

    @Column(name ="member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "product_seq", nullable = false)
    private Long productSeq;

    @Column(name="product_num", nullable = false)
    private Integer productNum;

}
