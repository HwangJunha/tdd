package com.around.tdd.vo;

import com.around.tdd.enums.ShippingStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Shipping")
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_seq", nullable = false)
    private Long shippingSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @Column(name = "shipping_dt")
    private LocalDateTime shippingDt;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShippingStatusEnum status;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "post")
    private String post;

    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ShippingLog> shippingLogs = new ArrayList<>();  // 리스트 초기화

}
