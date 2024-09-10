package com.around.tdd.controller;

import com.around.tdd.dto.request.ShippingRequest;
import com.around.tdd.service.ShippingService;
import com.around.tdd.vo.Shipping;
import com.around.tdd.enums.ShippingStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShippingController.class)
class ShippingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShippingService shippingService;

    private Shipping savedShipping;

    @BeforeEach
    void setUp() {
        savedShipping = Shipping.builder()
                .shippingSeq(1L)
                .address("강서구 화곡동 423-28")
                .detailAddress("B03호")
                .post("12345")
                .phone("010-2988-1162")
                .status(ShippingStatusEnum.SHIPPING_IN_PROGRESS)
                .build();
    }

    @Test
    @DisplayName("배송 생성 테스트")
    void createShipping() throws Exception {
        // Given
        ShippingRequest.ShippingSaveRequest saveRequest = new ShippingRequest.ShippingSaveRequest();
        saveRequest.setAddress("강서구 화곡동 423-28");
        saveRequest.setDetailAddress("B03호");
        saveRequest.setPost("12345");
        saveRequest.setPhone("010-2988-1162");
        saveRequest.setOrderId(1L);

        Mockito.when(shippingService.createShipping(any(ShippingRequest.ShippingSaveRequest.class)))
                .thenReturn(savedShipping);

        // When & Then
        mockMvc.perform(post("/api/v1/shipping/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"강서구 화곡동 423-28\", \"detailAddress\":\"B03호\", \"post\":\"12345\", \"phone\":\"010-2988-1162\", \"orderId\":1}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("강서구 화곡동 423-28"))
                .andExpect(jsonPath("$.detailAddress").value("B03호"))
                .andExpect(jsonPath("$.status").value(ShippingStatusEnum.SHIPPING_IN_PROGRESS.toString()));
    }

    @Test
    @DisplayName("배송 상태 변경 테스트")
    void changeShippingStatus() throws Exception {
        // Given
        ShippingStatusEnum updatedStatus = ShippingStatusEnum.SHIPPING_COMPLETED;
        savedShipping.setStatus(updatedStatus);

        Mockito.when(shippingService.changeShippingStatus(anyLong(), any(ShippingStatusEnum.class)))
                .thenReturn(savedShipping);

        // When & Then
        mockMvc.perform(put("/api/v1/shipping/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newStatus\":\"SHIPPING_COMPLETED\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ShippingStatusEnum.SHIPPING_COMPLETED.toString()));
    }

    @Test
    @DisplayName("배송 조회 테스트")
    void getShippingById() throws Exception {
        // Given
        Mockito.when(shippingService.getShippingById(anyLong()))
                .thenReturn(savedShipping);

        // When & Then
        mockMvc.perform(get("/api/v1/shipping/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("강서구 화곡동 423-28"))
                .andExpect(jsonPath("$.detailAddress").value("B03호"))
                .andExpect(jsonPath("$.post").value("12345"))
                .andExpect(jsonPath("$.phone").value("010-2988-1162"))
                .andExpect(jsonPath("$.status").value(ShippingStatusEnum.SHIPPING_IN_PROGRESS.toString()));
    }
}
