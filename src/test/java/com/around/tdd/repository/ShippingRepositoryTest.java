package com.around.tdd.repository;

import com.around.tdd.enums.ShippingStatusEnum;
import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@DataJpaTest
public class ShippingRepositoryTest {

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private OrderRepository orderRepository;

    private ShippingStatusEnum defaultShippingStatus;

    @BeforeEach
    public void setUp() {
        // 공통적으로 사용할 기본 상태를 설정
        defaultShippingStatus = ShippingStatusEnum.SHIPPING_IN_PROGRESS;
    }

    @Test
    public void testSaveShipping() {
        // Given
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = Shipping.builder()
                .order(savedOrder)
                .shippingDt(LocalDateTime.now())
                .status(defaultShippingStatus)  // Enum 사용
                .build();

        Shipping savedShipping = shippingRepository.save(shipping);

        // Then
        assertThat(savedShipping.getShippingSeq()).isNotNull();
        assertThat(savedShipping.getStatus()).isEqualTo(ShippingStatusEnum.SHIPPING_IN_PROGRESS);
    }

    @Test
    public void testFindById() {
        // Given
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = Shipping.builder()
                .order(savedOrder)
                .shippingDt(LocalDateTime.now())
                .address("강서구 화곡동 423-28")
                .detailAddress("B03호")
                .post("12345")
                .phone("010-2988-1162")
                .status(defaultShippingStatus)  // Enum 사용
                .build();

        Shipping savedShipping = shippingRepository.save(shipping);

        // When
        Optional<Shipping> foundShipping = shippingRepository.findById(savedShipping.getShippingSeq());

        // Then
        assertThat(foundShipping).isPresent();
        assertThat(foundShipping.get().getShippingSeq()).isEqualTo(savedShipping.getShippingSeq());
        assertThat(foundShipping.get().getStatus()).isEqualTo(ShippingStatusEnum.SHIPPING_IN_PROGRESS);
    }

    @Test
    public void testUpdateShipping() {
        // Given
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = Shipping.builder()
                .order(savedOrder)
                .shippingDt(LocalDateTime.now())
                .address("강서구 화곡동 423-28")
                .detailAddress("B03호")
                .post("12345")
                .phone("010-2988-1162")
                .status(defaultShippingStatus)  // Enum 사용
                .build();

        Shipping savedShipping = shippingRepository.save(shipping);

        // When
        savedShipping.setAddress("강서구 화곡동 413-8"); // 주소 업데이트
        Shipping updatedShipping = shippingRepository.save(savedShipping);

        // Then
        assertThat(updatedShipping.getAddress()).isEqualTo("강서구 화곡동 413-8");
        assertThat(updatedShipping.getStatus()).isEqualTo(ShippingStatusEnum.SHIPPING_IN_PROGRESS);
    }

    @Test
    public void testDeleteShipping() {
        // Given
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = Shipping.builder()
                .order(savedOrder)
                .shippingDt(LocalDateTime.now())
                .address("강서구 화곡동 423-28")
                .detailAddress("B03호")
                .post("12345")
                .phone("010-2988-1162")
                .status(defaultShippingStatus)  // Enum 사용
                .build();

        Shipping savedShipping = shippingRepository.save(shipping);

        // When
        shippingRepository.delete(savedShipping);
        Optional<Shipping> deletedShipping = shippingRepository.findById(savedShipping.getShippingSeq());

        // Then
        assertThat(deletedShipping).isNotPresent();
    }
}
