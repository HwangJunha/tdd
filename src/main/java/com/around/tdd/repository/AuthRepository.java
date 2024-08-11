package com.around.tdd.repository;

import com.around.tdd.vo.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface AuthRepository extends JpaRepository<Member, Long> {
}
