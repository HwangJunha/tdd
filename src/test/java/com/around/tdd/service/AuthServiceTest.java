package com.around.tdd.service;

import com.around.tdd.property.AuthProperty;
import com.around.tdd.repository.AuthRedisRepository;
import com.around.tdd.vo.RedisAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthRedisRepository authRedisRepository;

    @Mock
    private AuthProperty authProperty;

    @DisplayName("인증 번호 저장 테스트")
    @ParameterizedTest
    @CsvSource({"member-auth:1,111111","member-auth:2,222222"})
    void saveAuthTest(String redisKey, String authNumber){
        authService.saveAuth(redisKey, authNumber);
        verify(authRedisRepository, times(1)).save(any(RedisAuth.class));
    }

    @DisplayName("인증 번호 매치 테스트")
    @ParameterizedTest
    @CsvSource({"member-auth:1,111111","member-auth:2,222222"})
    void matchAuthTest(String redisKey, String authNumber){
        RedisAuth redisAuth = new RedisAuth(redisKey, authNumber);
        when(authRedisRepository.findById(redisKey)).thenReturn(Optional.of(redisAuth));
        boolean result = authService.matchAuth(redisKey, authNumber);
        assertThat(result).isTrue();
    }

    @DisplayName("인증 토큰 값 테스트")
    @ParameterizedTest
    @CsvSource({"1,1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014","2,60303ae22b998861bce3b28f33eec1be758a213c86c93c076dbe9f558c11c752"})
    void getTokenTest(int memberSeq, String matchToken){
        when(authProperty.getSalt()).thenReturn("test");
        String token = authService.getToken(memberSeq);
        assertThat(token).isNotNull();
        assertThat(token).hasSize(64);
        assertThat(token).isEqualTo(matchToken);
    }

    @DisplayName("인증 번호 유효 테스트")
    @Test
    void getAuthNumberTest(){
        String authNumber = authService.getAuthNumber();

        assertThat(authNumber).isNotNull();
        assertThat(authNumber).hasSize(6);
    }




}