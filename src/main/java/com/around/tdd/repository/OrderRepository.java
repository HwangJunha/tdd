package com.around.tdd.repository;

import com.around.tdd.vo.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
