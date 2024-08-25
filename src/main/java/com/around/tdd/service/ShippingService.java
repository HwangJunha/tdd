package com.around.tdd.service;

import com.around.tdd.vo.Shipping;
import com.around.tdd.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {

    @Autowired
    private ShippingRepository shippingRepository;

    public Shipping createShipping(Shipping shipping) {
        // Shipping과 ShippingLog를 함께 저장
        return shippingRepository.save(shipping);
    }
}
