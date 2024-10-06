package com.around.tdd.vo;

import com.around.tdd.enums.ShippingStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShippingStatusEnum status;
}
