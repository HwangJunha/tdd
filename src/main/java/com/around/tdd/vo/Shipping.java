package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "Shipping")
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_seq", nullable = false)
    private Long shippingSeq;

    @ManyToOne
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @Column(name = "shipping_dt")
    private LocalDateTime shippingDt;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "shipping_status_seq", nullable = false)
    private ShippingStatus shippingStatus;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "post")
    private String post;

    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShippingLog> shippingLogs;
}
