package com.around.tdd.service;

import com.around.tdd.dto.request.ShippingRequest;
import com.around.tdd.repository.OrderRepository;
import com.around.tdd.repository.ShippingLogRepository;
import com.around.tdd.repository.ShippingRepository;
import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import com.around.tdd.vo.ShippingLog;
import com.around.tdd.enums.ShippingStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @InjectMocks
    private ShippingService shippingService;

    @Mock
    private ShippingRepository shippingRepository;

    @Mock
    private ShippingLogRepository shippingLogRepository;

    @Mock
    private OrderRepository orderRepository;

    @Nested
    class CreateShippingTest {

        Order order;
        ShippingRequest.ShippingSaveRequest shippingSaveRequest;

        @BeforeEach
        void setUp() {
            order = Order.builder()
                    .orderSeq(1L)
                    .build();

            shippingSaveRequest = new ShippingRequest.ShippingSaveRequest();
            shippingSaveRequest.setAddress("강서구 화곡동 423-28");
            shippingSaveRequest.setDetailAddress("B03호");
            shippingSaveRequest.setPost("12345");
            shippingSaveRequest.setPhone("010-2988-1162");
            shippingSaveRequest.setOrderId(1L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
            when(shippingRepository.save(any(Shipping.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        @DisplayName("배송 생성 및 로그 등록 테스트")
        void createShippingWithLog() {
            Shipping createdShipping = shippingService.createShipping(shippingSaveRequest);

            assertThat(createdShipping).isNotNull();
            assertThat(createdShipping.getAddress()).isEqualTo("강서구 화곡동 423-28");
            assertThat(createdShipping.getStatus()).isEqualTo(ShippingStatusEnum.SHIPPING_IN_PROGRESS);

            verify(shippingRepository, times(1)).save(any(Shipping.class));
            verify(shippingLogRepository, times(1)).save(any(ShippingLog.class));
        }
    }

    @Nested
    class ChangeShippingStatusTest {

        Order order;
        Shipping savedShipping;
        ShippingStatusEnum initialStatus;
        ShippingStatusEnum updatedStatus;

        @BeforeEach
        void setUp() {
            order = Order.builder()
                    .orderSeq(1L)
                    .build();

            initialStatus = ShippingStatusEnum.SHIPPING_IN_PROGRESS;
            updatedStatus = ShippingStatusEnum.SHIPPING_COMPLETED;

            savedShipping = Shipping.builder()
                    .order(order)
                    .shippingDt(LocalDateTime.now())
                    .address("강서구 화곡동 423-28")
                    .detailAddress("B03호")
                    .post("12345")
                    .phone("010-2988-1162")
                    .status(initialStatus)
                    .build();

            when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(savedShipping));
            when(shippingRepository.save(any(Shipping.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        @DisplayName("배송 상태 변경 및 로그 등록 테스트")
        void changeShippingStatusWithLog() {
            Shipping updatedShipping = shippingService.changeShippingStatus(savedShipping.getShippingSeq(), updatedStatus);

            assertThat(updatedShipping.getStatus()).isEqualTo(ShippingStatusEnum.SHIPPING_COMPLETED);
            assertThat(updatedShipping.getAddress()).isEqualTo("강서구 화곡동 423-28");

            verify(shippingRepository, times(1)).save(updatedShipping);
            verify(shippingLogRepository, times(1)).save(any(ShippingLog.class));
        }
    }

    @Nested
    class GetShippingTest {

        Order order;
        Shipping savedShipping;

        @BeforeEach
        void setUp() {
            order = Order.builder()
                    .orderSeq(1L)
                    .build();

            savedShipping = Shipping.builder()
                    .order(order)
                    .shippingDt(LocalDateTime.now())
                    .address("강서구 화곡동 423-28")
                    .detailAddress("B03호")
                    .post("12345")
                    .phone("010-2988-1162")
                    .status(ShippingStatusEnum.SHIPPING_IN_PROGRESS)
                    .build();

            when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(savedShipping));
        }

        @Test
        @DisplayName("배송 조회 테스트")
        void getShippingById() {
            Long shippingId = 1L;

            Shipping foundShipping = shippingService.getShippingById(shippingId);

            assertThat(foundShipping).isNotNull();
            assertThat(foundShipping.getAddress()).isEqualTo("강서구 화곡동 423-28");
            assertThat(foundShipping.getDetailAddress()).isEqualTo("B03호");
            assertThat(foundShipping.getStatus()).isEqualTo(ShippingStatusEnum.SHIPPING_IN_PROGRESS);

            verify(shippingRepository, times(1)).findById(shippingId);
        }
    }
}
