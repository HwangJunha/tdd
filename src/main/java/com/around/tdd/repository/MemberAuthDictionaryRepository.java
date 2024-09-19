package com.around.tdd.repository;

import com.around.tdd.vo.MemberAuthDictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberAuthDictionaryRepository extends JpaRepository<MemberAuthDictionary, Long> {

    long countByMemberAuthDictionarySeqIn(List<Long> ids);

}
