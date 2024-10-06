package com.around.tdd.repository;

import com.around.tdd.vo.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveOrder() {
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);
        assertThat(savedOrder.getOrderSeq()).isNotNull();
    }

    @Test
    public void testFindById() {
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);

        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getOrderSeq());

        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getOrderSeq()).isEqualTo(savedOrder.getOrderSeq());
    }

    @Test
    public void testUpdateOrder() {
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);

        savedOrder.setOrderSeq(123L);
        Order updatedOrder = orderRepository.save(savedOrder);

        assertThat(updatedOrder.getOrderSeq()).isEqualTo(savedOrder.getOrderSeq());
    }

    @Test
    public void testDeleteOrder() {
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);

        orderRepository.delete(savedOrder);
        Optional<Order> deletedOrder = orderRepository.findById(savedOrder.getOrderSeq());

        assertThat(deletedOrder).isNotPresent();
    }

    @Test
    public void testSaveOrderWithoutRequiredField() {
        Order order = new Order();

        assertThrows(DataIntegrityViolationException.class, () -> {
            orderRepository.save(order);
        });
    }
}
