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

        // 배송 생성 시 기본 상태는 '배송시작'
        ShippingStatusEnum initialStatus = ShippingStatusEnum.SHIPPING_STARTED;

        // Shipping 객체 생성 및 저장
        Shipping shipping = Shipping.builder()
                .order(order)
                .shippingDt(LocalDateTime.now())
                .address(saveRequest.getAddress())
                .detailAddress(saveRequest.getDetailAddress())
                .post(saveRequest.getPost())
                .phone(saveRequest.getPhone())
                .status(initialStatus)
                .build();

        Shipping savedShipping = shippingRepository.save(shipping);

        // 배송 로그 기록
        ShippingLog log = ShippingLog.builder()
                .shipping(savedShipping)
                .shippingLogDt(LocalDateTime.now())
                .status(initialStatus)
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
