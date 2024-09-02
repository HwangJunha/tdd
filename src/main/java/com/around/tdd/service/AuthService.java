package com.around.tdd.service;

import com.around.tdd.property.AuthProperty;
import com.around.tdd.repository.AuthRedisRepository;
import com.around.tdd.vo.RedisAuth;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRedisRepository authRedisRepository;

    private final AuthProperty authProperty;

    /**
     * 인증관련 레디스 키 저장
     * @param redisKey - 인증 번호 레디스 키 값
     * @param authNumber - 인증 번호 value
     */
    public void saveAuth(String redisKey, String authNumber) {
        authRedisRepository.save(new RedisAuth(redisKey, authNumber));
    }

    /**
     * 인증관련 레디스 키 value 매칭
     * @param redisKey - 조회할 인증 번호 키
     * @param authValue - 인증 관련 value
     * @return true: 인증번호 일치, false: 인증번호 불일치
     */
    public boolean matchAuth(String redisKey, String authValue) {
        Optional<RedisAuth> authToken = authRedisRepository.findById(redisKey);
        if (authToken.isPresent()) {
            return authToken.get().getValue().equals(authValue);
        }
        return false;
    }

    /**
     * 토큰 생성
     * @param memberSeq - 사용자 번호
     * @return salt + memberSeq와 SHA256으로 암호화된 token 반환
     */
    public String getToken(int memberSeq) {
        String authTokenText = authProperty.getSalt()+ memberSeq;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(authTokenText.getBytes());
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 암호화된 값 읽어들이는 메소드
     * @param bytes
     * @return
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    /**
     * 랜덤한 6자리 인증 번호를 반환
     * @return 6자리 숫자로 된 번호 반환
     */
    public String getAuthNumber() {
        return RandomStringUtils.randomNumeric(6);
    }

}
