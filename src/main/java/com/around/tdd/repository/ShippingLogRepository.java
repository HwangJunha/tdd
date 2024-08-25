package com.around.tdd.repository;

import com.around.tdd.vo.ShippingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingLogRepository extends JpaRepository<ShippingLog, Long> {
}
