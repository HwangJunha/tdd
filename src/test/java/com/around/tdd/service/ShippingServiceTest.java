package com.around.tdd.service;

import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import com.around.tdd.vo.ShippingLog;
import com.around.tdd.vo.ShippingStatus;
import com.around.tdd.repository.ShippingRepository;
import com.around.tdd.repository.ShippingStatusRepository;
import com.around.tdd.repository.ShippingLogRepository;
import com.around.tdd.repository.OrderRepository; // OrderRepository 추가
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
@Transactional
public class ShippingServiceTest {

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;

    @Autowired
    private ShippingLogRepository shippingLogRepository;

    @Autowired
    private OrderRepository orderRepository; // OrderRepository 추가

    @Test
    public void testCreateShippingWithLog() {
        // Given
        Order order = new Order();
        // Order 엔티티를 먼저 저장
        Order savedOrder = orderRepository.save(order);

        ShippingStatus status = new ShippingStatus();
        status.setDescription("배송중");

        // ShippingStatus를 먼저 저장
        ShippingStatus savedStatus = shippingStatusRepository.save(status);

        Shipping shipping = new Shipping();
        shipping.setOrder(savedOrder);  // 저장된 Order를 설정
        shipping.setShippingDt(LocalDateTime.now());
        shipping.setAddress("강서구 화곡동 423-28");
        shipping.setDetailAddress("B03호");
        shipping.setPost("12345");
        shipping.setPhone("010-2988-1162");
        shipping.setShippingStatus(savedStatus);

        // Shipping을 먼저 저장
        Shipping savedShipping = shippingRepository.save(shipping);

        // ShippingLog 생성 후 Shipping에 추가
        ShippingLog log = new ShippingLog();
        log.setShipping(savedShipping);
        log.setShippingLogDt(LocalDateTime.now());
        log.setShippingStatus(savedStatus);

        savedShipping.getShippingLogs().add(log);

        // Shipping과 ShippingLog를 함께 다시 저장
        Shipping finalSavedShipping = shippingRepository.save(savedShipping);

        assertThat(finalSavedShipping.getShippingSeq()).isNotNull();
        assertThat(finalSavedShipping.getOrder()).isEqualTo(savedOrder);
        assertThat(finalSavedShipping.getAddress()).isEqualTo("강서구 화곡동 423-28");

        // 저장된 ShippingLog 확인
        assertThat(finalSavedShipping.getShippingLogs()).isNotEmpty();
        assertThat(finalSavedShipping.getShippingLogs().get(0).getShippingStatus()).isEqualTo(savedStatus);
    }
}
