package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "PaymentLog")
public class PaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_log_seq", nullable = false)
    private Long paymentLogSeq;

    @ManyToOne
    @JoinColumn(name = "payment_seq", nullable = false)
    private Payment payment;

    @Column(name = "payment_log_dt")
    private LocalDateTime paymentLogDt;

    @ManyToOne
    @JoinColumn(name = "payment_status_seq", nullable = false)
    private PaymentStatus paymentStatus;
}
