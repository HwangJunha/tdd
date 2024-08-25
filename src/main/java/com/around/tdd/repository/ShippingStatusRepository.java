package com.around.tdd.repository;

import com.around.tdd.vo.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingStatusRepository extends JpaRepository<ShippingStatus, Long> {
}
