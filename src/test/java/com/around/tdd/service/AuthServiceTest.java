package com.around.tdd.service;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.property.AuthProperty;
import com.around.tdd.repository.AuthRedisRepository;
import com.around.tdd.repository.MemberAuthDictionaryRepository;
import com.around.tdd.repository.MemberAuthRepository;
import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthRedisRepository authRedisRepository;

    @Mock
    private AuthProperty authProperty;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberAuthDictionaryRepository memberAuthDictionaryRepository;

    @Mock
    private MemberAuthRepository memberAuthRepository;

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

    @Nested
    class matchMemberAuthDictionaryTest{
        private Member member;
        private MemberAuthDictionary memberAuthDictionary;
        private MemberAuth memberAuth;
        @BeforeEach
        void setUp(){
            // 테스트에 사용할 데이터 초기화
            member = Member
                    .builder()
                    .build();
            memberAuthDictionary = MemberAuthDictionary
                    .builder()
                    .build();
            memberAuth = new MemberAuth();
            memberAuth.setMemberAuthId(new MemberAuthId(1L, 1L)); // Composite Key 사용 예시
        }

        @Test
        @DisplayName("권한 매칭 성공")
        void testMatchMemberAuthDictionary_Success() {
            // given
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(memberAuthDictionaryRepository.findById(1L)).thenReturn(Optional.of(memberAuthDictionary));
            when(memberAuthRepository.save(memberAuth)).thenReturn(memberAuth);

            // when
            ResponseEntity<ApiResponse<MemberAuth>> response = authService.matchMemberAuthDictionary(memberAuth);

            // then
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("입력된 권한이 모두 저장되었습니다.", response.getBody().getMessage());
            assertEquals(memberAuth, response.getBody().getData().get("savedMemberAuth"));
        }

        @Test
        @DisplayName("사용자 없음 테스트")
        void testMatchMemberAuthDictionary_MemberNotFound() {
            // given
            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            // when
            ResponseEntity<ApiResponse<MemberAuth>> response = authService.matchMemberAuthDictionary(memberAuth);

            // then
            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertEquals("사용자 없음", response.getBody().getMessage());
        }

        @Test
        @DisplayName("권한이 정의 되어 있지 않은 테스트")
        void testMatchMemberAuthDictionary_AuthDictionaryNotFound() {
            // given
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(memberAuthDictionaryRepository.findById(1L)).thenReturn(Optional.empty());

            // when
            ResponseEntity<ApiResponse<MemberAuth>> response = authService.matchMemberAuthDictionary(memberAuth);

            // then
            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertEquals("일부 권한이 정의 되어 있지 않음", response.getBody().getMessage());
        }

    }

    @Nested
    class CheckMemberAuthTest{

        private MemberAuthId memberAuthId;
        private MemberAuth memberAuth;
        private Long memberSeq = 1L;
        private Long memberAuthDictionarySeq = 1L;

        @BeforeEach
        void setUp(){
            //given
            memberAuthId = new MemberAuthId(memberSeq, memberAuthDictionarySeq);
            memberAuth = new MemberAuth();
            memberAuth.setMemberAuthId(memberAuthId);
        }

        @Test
        @DisplayName("권한이 존재하는 테스트")
        void testCheckMemberAuthSuccess() {
            //when
            when(memberAuthRepository.findById(memberAuthId)).thenReturn(Optional.of(memberAuth));
            //then
            Boolean result = authService.checkMemberAuth(memberSeq, memberAuthDictionarySeq);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("권한이 존재하지 않은 테스트 테스트")
        void testCheckMemberAuthFail() {
            //when
            when(memberAuthRepository.findById(memberAuthId)).thenReturn(Optional.empty());
            //then
            Boolean result = authService.checkMemberAuth(memberSeq, memberAuthDictionarySeq);
            assertThat(result).isFalse();
        }
    }

    @Nested
    class RemoveMemberAuthTest{

        private MemberAuthId memberAuthId;
        private MemberAuth memberAuth;
        private Long memberSeq = 1L;
        private Long memberAuthDictionarySeq = 1L;

        @BeforeEach
        void setUp(){
            //given
            memberAuthId = new MemberAuthId(memberSeq, memberAuthDictionarySeq);
            memberAuth = new MemberAuth();
            memberAuth.setMemberAuthId(memberAuthId);
        }

        @Test
        @DisplayName("권한이 존재하지 않은 테스트")
        void testRemoveMemberAuthFail() {
            //when
            when(memberAuthRepository.findById(memberAuthId)).thenReturn(Optional.empty());
            //then
            var result = authService.removeMemberAuth(memberSeq, memberAuthDictionarySeq);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("권한이 존재하여 삭제가 된 테스트")
        void testRemoveMemberAuthSuccess() {
            //when
            when(memberAuthRepository.findById(memberAuthId)).thenReturn(Optional.of(memberAuth));
            //then
            var result = authService.removeMemberAuth(memberSeq, memberAuthDictionarySeq);
            assertThat(result.isPresent()).isTrue();
            assertThat(result.get()).isEqualTo(memberAuth);
        }
    }

}