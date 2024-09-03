package com.around.tdd.service;

import com.around.tdd.dto.ShippingDTO;
import com.around.tdd.repository.ShippingLogRepository;
import com.around.tdd.repository.ShippingStatusRepository;
import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import com.around.tdd.repository.ShippingRepository;
import com.around.tdd.vo.ShippingLog;
import com.around.tdd.vo.ShippingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShippingService {

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;

    @Autowired
    private ShippingLogRepository shippingLogRepository;


    /**
     * Shipping 생성
     * @param shippingDTO - 배송 dto
     * @param order - 주문 객체
     * @return - 생성된 배송 dto
     */
    public Shipping createShipping(ShippingDTO shippingDTO, Order order) {
        // 기본 배송 상태
        ShippingStatus initialStatus = shippingStatusRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Shipping status not found"));

        Shipping shipping = Shipping.builder()
                .order(order)
                .shippingDt(LocalDateTime.now())
                .address(shippingDTO.getAddress())
                .detailAddress(shippingDTO.getDetailAddress())
                .post(shippingDTO.getPost())
                .phone(shippingDTO.getPhone())
                .shippingStatus(initialStatus)
                .build();

        // 배송 저장
        Shipping savedShipping = shippingRepository.save(shipping);
        ShippingLog log = ShippingLog.builder()
                .shipping(savedShipping)
                .shippingLogDt(LocalDateTime.now())
                .shippingStatus(initialStatus)
                .build();

        shippingLogRepository.save(log);

        return savedShipping;
    }

    /**
     * Shipping 수정
     * @param shippingId - 배송 id
     * @param shippingDTO - 배송 dto
     * @param statusId - 상태 id
     * @return shipping
     */
    public Shipping modifyShipping(Long shippingId, ShippingDTO shippingDTO, Long statusId) {

        // shipping 검색
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new IllegalArgumentException("Shipping Not Found"));
        
        if (statusId != null) {
            ShippingStatus newStatus = shippingStatusRepository.findById(statusId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipping status not found"));

            shipping.setShippingStatus(newStatus);

            ShippingLog log = ShippingLog.builder()
                    .shipping(shipping)
                    .shippingLogDt(LocalDateTime.now())
                    .shippingStatus(newStatus)
                    .build();
            shippingLogRepository.save(log);
        }

        // 주소 정보 업데이트
        shipping.setAddress(shippingDTO.getAddress());
        shipping.setDetailAddress(shippingDTO.getDetailAddress());
        shipping.setPost(shippingDTO.getPost());
        shipping.setPhone(shippingDTO.getPhone());

        // 수정된 Shipping 저장
        return shippingRepository.save(shipping);
    }
}
