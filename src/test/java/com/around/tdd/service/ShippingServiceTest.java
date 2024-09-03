package com.around.tdd.service;

import com.around.tdd.dto.ShippingDTO;
import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import com.around.tdd.vo.ShippingLog;
import com.around.tdd.vo.ShippingStatus;
import com.around.tdd.repository.ShippingRepository;
import com.around.tdd.repository.ShippingStatusRepository;
import com.around.tdd.repository.ShippingLogRepository;
import com.around.tdd.repository.OrderRepository; // OrderRepository 추가
import org.aspectj.weaver.ast.Or;
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
        Order order = new Order();

        // ShippingStatus 저장
        Order savedOrder = orderRepository.save(order);

        ShippingStatus status = ShippingStatus.builder()
                .description("배송중")
                .build();

        // ShippingStatus 저장
        ShippingStatus savedStatus = shippingStatusRepository.save(status);

        ShippingDTO shippingDTO = ShippingDTO.builder()
                .address("강서구 화곡동 423-28")
                .detailAddress("B03호")
                .post("12345")
                .phone("010-2988-1162")
                .build();

        // Shipping 생성 및 저장
        Shipping savedShipping = shippingService.createShipping(shippingDTO, savedOrder);

        // 빌더 패턴을 사용해 ShippingLog 객체 생성
        ShippingLog log = ShippingLog.builder()
                .shipping(savedShipping)
                .shippingLogDt(LocalDateTime.now())
                .shippingStatus(savedStatus)
                .build();

        savedShipping.getShippingLogs().add(log);
        shippingLogRepository.save(log);

        assertThat(savedShipping.getShippingSeq()).isNotNull();
        assertThat(savedShipping.getOrder()).isEqualTo(savedOrder);
        assertThat(savedShipping.getAddress()).isEqualTo("강서구 화곡동 423-28");

        // 저장된 ShippingLog 확인
        assertThat(savedShipping.getShippingLogs()).isNotEmpty();
        assertThat(savedShipping.getShippingLogs().get(0).getShippingStatus()).isEqualTo(savedStatus);
    }

    @Test
    public void testModifyShipping() {
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);  // Order 저장

        ShippingStatus initialStatus = ShippingStatus.builder()
                .description("배송중")
                .build();
        ShippingStatus savedInitialStatus = shippingStatusRepository.save(initialStatus);  // 초기 ShippingStatus 저장

        ShippingDTO shippingDTO = ShippingDTO.builder()
                .address("강서구 화곡동 423-28")
                .detailAddress("B03호")
                .post("12345")
                .phone("010-2988-1162")
                .build();

        Shipping savedShipping = shippingService.createShipping(shippingDTO, savedOrder);  // Shipping 생성 및 저장

        ShippingStatus updatedStatus = ShippingStatus.builder()
                .description("배송완료")
                .build();
        ShippingStatus savedUpdatedStatus = shippingStatusRepository.save(updatedStatus);  // 수정된 ShippingStatus 저장

        ShippingDTO updatedShippingDTO = ShippingDTO.builder()
                .address("서울시 강서구 등촌동")
                .detailAddress("101호")
                .post("67890")
                .phone("010-1234-5678")
                .build();

        Shipping modifiedShipping = shippingService.modifyShipping(savedShipping.getShippingSeq(), updatedShippingDTO, savedUpdatedStatus.getShippingStatusSeq());

        assertThat(modifiedShipping.getShippingSeq()).isEqualTo(savedShipping.getShippingSeq());
        assertThat(modifiedShipping.getShippingStatus().getDescription()).isEqualTo("배송완료");
        assertThat(modifiedShipping.getAddress()).isEqualTo("서울시 강서구 등촌동");
        assertThat(modifiedShipping.getDetailAddress()).isEqualTo("101호");
        assertThat(modifiedShipping.getPost()).isEqualTo("67890");
        assertThat(modifiedShipping.getPhone()).isEqualTo("010-1234-5678");

        // 수정 후 로그가 남겨졌는지 확인
        assertThat(modifiedShipping.getShippingLogs()).isNotEmpty();
        assertThat(modifiedShipping.getShippingLogs().get(modifiedShipping.getShippingLogs().size() - 1).getShippingStatus()).isEqualTo(savedUpdatedStatus);
    }

}
