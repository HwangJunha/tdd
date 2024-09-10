package com.around.tdd.service;

import com.around.tdd.dto.request.ShippingRequest;
import com.around.tdd.enums.ShippingStatusEnum;
import com.around.tdd.repository.OrderRepository;
import com.around.tdd.repository.ShippingLogRepository;
import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import com.around.tdd.repository.ShippingRepository;
import com.around.tdd.vo.ShippingLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShippingService {

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private ShippingLogRepository shippingLogRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 배송 생성
    public Shipping createShipping(ShippingRequest.ShippingSaveRequest saveRequest) {
        // 주문 조회
        Long orderId = saveRequest.getOrderId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문정보를 찾을 수 없습니다."));

        // Shipping 엔티티 생성
        Shipping shipping = saveRequest.toEntity(order);

        // 배송 저장
        Shipping savedShipping = shippingRepository.save(shipping);

        // 배송 로그 생성 및 저장
        ShippingLog log = ShippingLog.builder()
                .shipping(savedShipping)
                .shippingLogDt(LocalDateTime.now())
                .status(savedShipping.getStatus())
                .build();

        shippingLogRepository.save(log);

        return savedShipping;
    }

    // 배송 상태 변경
    public Shipping changeShippingStatus(Long shippingId, ShippingStatusEnum newStatus) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new IllegalArgumentException("배송정보를 찾을 수 없습니다."));

        // 상태 변경
        shipping.setStatus(newStatus);

        // 로그 저장
        ShippingLog log = ShippingLog.builder()
                .shipping(shipping)
                .shippingLogDt(LocalDateTime.now())
                .status(newStatus)
                .build();

        shippingLogRepository.save(log);

        return shippingRepository.save(shipping);
    }

    // 배송 조회
    public Shipping getShippingById(Long shippingId) {
        return shippingRepository.findById(shippingId)
                .orElseThrow(() -> new IllegalArgumentException("배송정보를 찾을 수 없습니다."));
    }

}
