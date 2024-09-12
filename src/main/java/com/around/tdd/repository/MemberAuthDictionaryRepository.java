package com.around.tdd.repository;

import com.around.tdd.vo.MemberAuthDictionary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAuthDictionaryRepository extends JpaRepository<MemberAuthDictionary, Long> {
}
