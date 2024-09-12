package com.around.tdd.repository;

import com.around.tdd.vo.MemberAuthDictionary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@DataJpaTest
class MemberAuthDictionaryRepositoryTest {

    @Autowired
    private MemberAuthDictionaryRepository memberAuthDictionaryRepository;

    @Nested
    class MemberAuthDictionaryRepositoryInsertTest{
        private MemberAuthDictionary memberAuthDictionary1;
        private MemberAuthDictionary memberAuthDictionary2;

        @Test
        @DisplayName("회원 권한 사전 테이블 입력")
        void MemberAuthDictionaryRepositoryInsertTest(){
            memberAuthDictionary1 = MemberAuthDictionary
                    .builder()
                    .authName("임시 권한1")
                    .build();

            memberAuthDictionary2 = MemberAuthDictionary
                    .builder()
                    .authName("임시 권한2")
                    .build();
            var saveMemberAuthDictionary1 = memberAuthDictionaryRepository.save(memberAuthDictionary1);
            var saveMemberAuthDictionary2 = memberAuthDictionaryRepository.save(memberAuthDictionary2);


            var listMemberAuthDictionary = memberAuthDictionaryRepository.findAll();

            assertThat(listMemberAuthDictionary).contains(saveMemberAuthDictionary1);
            assertThat(listMemberAuthDictionary).contains(saveMemberAuthDictionary2);
        }
    }

}