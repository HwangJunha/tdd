package com.around.tdd.controller;

import com.around.tdd.dto.request.ShippingRequest;
import com.around.tdd.service.ShippingService;
import com.around.tdd.vo.Shipping;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    /**
     * 배송 생성 API
     * @param saveRequest - 배송 요청 정보
     * @return 생성된 배송 정보
     */
    @PostMapping("/v1/save")
    public ResponseEntity<Shipping> createShipping(@RequestBody @Valid ShippingRequest.ShippingSaveRequest saveRequest) {
        Shipping savedShipping = shippingService.createShipping(saveRequest);
        return ResponseEntity.ok(savedShipping);
    }

    /**
     * 배송 상태 변경 API
     * @param shippingId - 배송 ID
     * @param statusChangeRequest - 변경할 상태
     * @return 변경된 배송 정보
     */
    @PutMapping("/v1/{id}/status")
    public ResponseEntity<Shipping> changeShippingStatus(@PathVariable("id") Long shippingId,
                                                         @RequestBody @Valid ShippingRequest.ShippingStatusChangeRequest statusChangeRequest) {
        Shipping updatedShipping = shippingService.changeShippingStatus(shippingId, statusChangeRequest.getNewStatus());
        return ResponseEntity.ok(updatedShipping);
    }

    /**
     * 배송 조회 API
     * @param shippingId - 배송 ID
     * @return 조회된 배송 정보
     */
    @GetMapping("/v1/{id}")
    public ResponseEntity<Shipping> getShippingById(@PathVariable("id") Long shippingId) {
        // 서비스에서 배송 조회
        Shipping shipping = shippingService.getShippingById(shippingId);
        return ResponseEntity.ok(shipping);
    }
}

