package com.around.tdd.repository;

import com.around.tdd.vo.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByMemberSeq(Long memberSeq);
}
