package com.around.tdd.repository;

import com.around.tdd.vo.RedisAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@DataRedisTest
class AuthRedisRepositoryTest {

    @Autowired
    private AuthRedisRepository authRedisRepository;

    @ParameterizedTest
    @CsvSource({"1,111111", "2,222222", "3,333333"})
    @DisplayName("인증 번호 6자리를 redis의 저장")
    void authNumberSaveTest(Integer memberSeq, String authNumber){
        //given
        String id = "member-auth:"+memberSeq;
        String value = authNumber;
        RedisAuth redisAuth = new RedisAuth(id, value);
        authRedisRepository.save(redisAuth);
        Optional<RedisAuth> resultRedisAuthToken = authRedisRepository.findById(id);
        assertThat(resultRedisAuthToken.get()).isEqualTo(redisAuth);
    }


}