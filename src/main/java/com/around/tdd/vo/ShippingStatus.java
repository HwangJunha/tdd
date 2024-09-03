package com.around.tdd.vo;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ShippingStatus")
public class ShippingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_status_seq", nullable = false)
    private Long shippingStatusSeq;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "shippingStatus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Shipping> shippings;

    @OneToMany(mappedBy = "shippingStatus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShippingLog> shippingLogs;
}
