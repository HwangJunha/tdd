package com.around.tdd.repository;

import com.around.tdd.vo.MemberAuth;
import com.around.tdd.vo.MemberAuthId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAuthRepository extends JpaRepository<MemberAuth, MemberAuthId> {
}
