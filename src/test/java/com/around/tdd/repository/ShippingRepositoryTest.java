package com.around.tdd.repository;

import com.around.tdd.vo.Order;
import com.around.tdd.vo.Shipping;
import com.around.tdd.vo.ShippingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ShippingRepositoryTest {

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;

    private ShippingStatus defaultShippingStatus;

    @BeforeEach
    public void setUp() {
        // 공통적으로 사용할 ShippingStatus를 미리 생성
        defaultShippingStatus = new ShippingStatus();
        defaultShippingStatus.setDescription("배송중");
        defaultShippingStatus = shippingStatusRepository.save(defaultShippingStatus);
    }

    @Test
    public void testSaveShipping() {
        // Given
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = new Shipping();
        shipping.setOrder(savedOrder);
        shipping.setShippingDt(LocalDateTime.now());

        shipping.setShippingStatus(defaultShippingStatus);
        Shipping savedShipping = shippingRepository.save(shipping);
        assertThat(savedShipping.getShippingSeq()).isNotNull();
    }

    @Test
    public void testFindById() {
        // Given
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = new Shipping();
        shipping.setOrder(savedOrder);
        shipping.setShippingDt(LocalDateTime.now());
        shipping.setAddress("강서구 화곡동 423-28");
        shipping.setDetailAddress("B03호");
        shipping.setPost("12345");
        shipping.setPhone("010-2988-1162");
        shipping.setShippingStatus(defaultShippingStatus);
        Shipping savedShipping = shippingRepository.save(shipping);

        Optional<Shipping> foundShipping = shippingRepository.findById(savedShipping.getShippingSeq());
        assertThat(foundShipping).isPresent();
        assertThat(foundShipping.get().getShippingSeq()).isEqualTo(savedShipping.getShippingSeq());
    }

    @Test
    public void testUpdateShipping() {
        // Given
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = new Shipping();
        shipping.setOrder(savedOrder);
        shipping.setShippingDt(LocalDateTime.now());
        shipping.setAddress("강서구 화곡동 423-28");
        shipping.setDetailAddress("B03호");
        shipping.setPost("12345");
        shipping.setPhone("010-2988-1162");
        shipping.setShippingStatus(defaultShippingStatus);
        Shipping savedShipping = shippingRepository.save(shipping);

        savedShipping.setAddress("강서구 화곡동 413-8"); // 주소 업데이트
        Shipping updatedShipping = shippingRepository.save(savedShipping);
        assertThat(updatedShipping.getAddress()).isEqualTo("강서구 화곡동 413-8");
    }

    @Test
    public void testDeleteShipping() {
        Order order = new Order();
        Order savedOrder = orderRepository.save(order); // Order 먼저 저장

        Shipping shipping = new Shipping();
        shipping.setOrder(savedOrder);
        shipping.setShippingDt(LocalDateTime.now());
        shipping.setAddress("강서구 화곡동 423-28");
        shipping.setDetailAddress("B03호");
        shipping.setPost("12345");
        shipping.setPhone("010-2988-1162");
        shipping.setShippingStatus(defaultShippingStatus);
        Shipping savedShipping = shippingRepository.save(shipping);

        shippingRepository.delete(savedShipping);
        Optional<Shipping> deletedShipping = shippingRepository.findById(savedShipping.getShippingSeq());

        assertThat(deletedShipping).isNotPresent();
    }
}
