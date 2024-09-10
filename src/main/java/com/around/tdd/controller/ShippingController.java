package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.dto.request.ShippingRequest;
import com.around.tdd.service.ShippingService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.Shipping;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Shipping>> createShipping(@RequestBody @Valid ShippingRequest.ShippingSaveRequest saveRequest) {
        HttpHeaders headers = HttpUtil.createJsonHeaders();

        Shipping savedShipping = shippingService.createShipping(saveRequest);

        Map<String, Shipping> responseData = new HashMap<>();
        responseData.put("shipping", savedShipping);

        ApiResponse<Shipping> response = new ApiResponse<>(responseData, "배송 생성 성공", HttpStatus.CREATED);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    /**
     * 배송 상태 변경 API
     * @param shippingId - 배송 ID
     * @param statusChangeRequest - 변경할 상태
     * @return 변경된 배송 정보
     */
    @PutMapping("/v1/{id}/status")
    public ResponseEntity<ApiResponse<Shipping>> changeShippingStatus(@PathVariable("id") Long shippingId,
                                                                      @RequestBody @Valid ShippingRequest.ShippingStatusChangeRequest statusChangeRequest) {
        HttpHeaders headers = HttpUtil.createJsonHeaders();

        Shipping updatedShipping = shippingService.changeShippingStatus(shippingId, statusChangeRequest.getNewStatus());

        Map<String, Shipping> responseData = new HashMap<>();
        responseData.put("shipping", updatedShipping);

        ApiResponse<Shipping> response = new ApiResponse<>(responseData, "배송 상태 변경 성공", HttpStatus.OK);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * 배송 조회 API
     * @param shippingId - 배송 ID
     * @return 조회된 배송 정보
     */
    @GetMapping("/v1/{id}")
    public ResponseEntity<ApiResponse<Shipping>> getShippingById(@PathVariable("id") Long shippingId) {
        HttpHeaders headers = HttpUtil.createJsonHeaders();

        Shipping shipping = shippingService.getShippingById(shippingId);

        Map<String, Shipping> responseData = new HashMap<>();
        responseData.put("shipping", shipping);

        ApiResponse<Shipping> response = new ApiResponse<>(responseData, "배송 조회 성공", HttpStatus.OK);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}

