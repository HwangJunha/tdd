package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_seq", nullable = false)
    private Long paymentSeq;

    @ManyToOne
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @Column(name = "payment_dt")
    private LocalDateTime paymentDt;

    @Column(name = "amount")
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "payment_status_seq", nullable = false)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentLog> paymentLogs;
}
