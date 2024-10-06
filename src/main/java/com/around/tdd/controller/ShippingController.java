package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.dto.request.ShippingRequest;
import com.around.tdd.enums.ShippingStatusEnum;
import com.around.tdd.service.ShippingService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.Shipping;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleEnumConversionError(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException && ((InvalidFormatException) cause).getTargetType().isEnum()) {
            String message = "유효하지 않은 배송 상태 값입니다. 가능한 값: "
                    + Arrays.toString(ShippingStatusEnum.values());

            ApiResponse<String> response = new ApiResponse<>(null, message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpUtil.createJsonHeaders(), HttpStatus.BAD_REQUEST);
        }

        ApiResponse<String> response = new ApiResponse<>(null, "요청 처리 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpUtil.createJsonHeaders(), HttpStatus.BAD_REQUEST);
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

