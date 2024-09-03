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

    private ShippingLogRepository shippingLogRepository;


    // 배송 생성
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


        // Shipping과 ShippingLog를 함께 저장
        return shippingRepository.save(shipping);
    }

    public Shipping modifyShipping(Shipping shipping) {
        // Shipping과 ShippingLog를 함께 저장
        return shippingRepository.save(shipping);
    }
}
