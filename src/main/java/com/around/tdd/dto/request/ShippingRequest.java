package com.around.tdd.dto.request;

import com.around.tdd.enums.ShippingStatusEnum;
import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ShippingRequest {

    @Getter
    @Setter
    public static class ShippingSaveRequest {

        @NotBlank(message = "주소를 입력해 주세요.")
        private String address;

        @NotBlank(message = "상세 주소를 입력해 주세요.")
        private String detailAddress;

        @NotBlank(message = "우편번호를 입력해 주세요.")
        private String post;

        @NotBlank(message = "전화번호를 입력해 주세요.")
        private String phone;

        @NotNull(message = "주문 ID를 입력해 주세요.")
        private Long orderId;

        // 엔티티 변환
        public Shipping toEntity(Order order) {
            return Shipping.builder()
                    .order(order)
                    .shippingDt(LocalDateTime.now())
                    .address(address)
                    .detailAddress(detailAddress)
                    .post(post)
                    .phone(phone)
                    .status(ShippingStatusEnum.SHIPPING_IN_PROGRESS)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class ShippingStatusChangeRequest {

        @NotNull(message = "배송 상태를 입력해 주세요.")
        private ShippingStatusEnum newStatus;
    }
}

