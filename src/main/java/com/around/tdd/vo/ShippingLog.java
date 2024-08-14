package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ShippingLog")
public class ShippingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_log_seq", nullable = false)
    private Long shippingLogSeq;

    @ManyToOne
    @JoinColumn(name = "shipping_seq", nullable = false)
    private Shipping shipping;

    @Column(name = "shipping_log_dt")
    private LocalDateTime shippingLogDt;

    @ManyToOne
    @JoinColumn(name = "shipping_status_seq", nullable = false)
    private ShippingStatus shippingStatus;
}
