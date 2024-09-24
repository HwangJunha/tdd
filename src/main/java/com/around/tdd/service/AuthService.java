package com.around.tdd.service;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.property.AuthProperty;
import com.around.tdd.repository.AuthRedisRepository;
import com.around.tdd.repository.MemberAuthDictionaryRepository;
import com.around.tdd.repository.MemberAuthRepository;
import com.around.tdd.repository.MemberRepository;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.MemberAuth;
import com.around.tdd.vo.MemberAuthDictionary;
import com.around.tdd.vo.MemberAuthId;
import com.around.tdd.vo.RedisAuth;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRedisRepository authRedisRepository;
    private final AuthProperty authProperty;
    private final MemberAuthDictionaryRepository memberAuthDictionaryRepository;
    private final MemberRepository memberRepository;
    private final MemberAuthRepository memberAuthRepository;

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

    /**
     * 사용자 권한을 확인한다.
     * @param memberAuthDictionarySeq - 사용자 권한 번호
     * @return 사용자 권한 확인
     */
    public Optional<MemberAuthDictionary> getMemberAuthDictionary(Long memberAuthDictionarySeq) {
        return memberAuthDictionaryRepository.findById(memberAuthDictionarySeq);
    }

    /**
     * 입력할 사용자 권한이 모두 있는지 확인한다
     * @param memberDictionarySeqs - 입력할 사용자 권한이 모두 있는지 확인한다
     * @return 권한 개수 반환
     */
    public Long getMemberAuthDictionarySeqs(List<Long> memberDictionarySeqs) {
        return memberAuthDictionaryRepository.countByMemberAuthDictionarySeqIn(memberDictionarySeqs);
    }


    /**
     * 사용자 권한 사전 테이블 값을 입력한다.
     * @param memberAuthDictionary - 사용자 권한 entity
     * @return 입력된 사용자 권한
     */
    public MemberAuthDictionary insertMemberAuthDictionary(MemberAuthDictionary memberAuthDictionary){
        return memberAuthDictionaryRepository.save(memberAuthDictionary);
    }

    /**
     * 사용자 권한 매치
     * @param memberAuth - 입력할 권한
     * @return 입력된 사용자 권한들
     */
    @Transactional
    public ResponseEntity<ApiResponse<MemberAuth>> matchMemberAuthDictionary(MemberAuth memberAuth) {
        var optionalMember = memberRepository.findById(memberAuth.getMemberAuthId().getMemberSeq());
        if (optionalMember.isEmpty()) {
            return HttpUtil.createApiResponse(null, "", "사용자 없음", HttpStatus.NO_CONTENT);
        }

        var optionalMemberAuthDictionary = memberAuthDictionaryRepository.findById(memberAuth.getMemberAuthId().getMemberAuthDictionarySeq());
        if(optionalMemberAuthDictionary.isEmpty()){
            return HttpUtil.createApiResponse(null, "", "일부 권한이 정의 되어 있지 않음", HttpStatus.NO_CONTENT);
        }
        memberAuth.setMember(optionalMember.get());
        memberAuth.setMemberAuthDictionary(optionalMemberAuthDictionary.get());
        var savedMemberAuth = memberAuthRepository.save(memberAuth);
        return HttpUtil.createApiResponse(savedMemberAuth, "savedMemberAuth", "입력된 권한이 모두 저장되었습니다.", HttpStatus.CREATED);
    }

    /**
     * 사용자 권한 확인 메서드
     * @param memberSeq - 사용자 번호
     * @param memberAuthDictionarySeq - 권한 번호
     * @return true: 권한 있음, false 권한 없음
     */
    @Cacheable(value="checkMemberAuth", key="#memberSeq + '_'+ #memberAuthDictionarySeq")
    public Boolean checkMemberAuth(Long memberSeq, Long memberAuthDictionarySeq) {
        var optionalMemberAuth = memberAuthRepository.findById(new MemberAuthId(memberSeq, memberAuthDictionarySeq));
        if(optionalMemberAuth.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * 사용자 권한 삭제 메서드
     * @param memberSeq - 사용자 번호
     * @param memberAuthDictionarySeq - 권한 번호
     * @return 삭제된 권한 반환
     */
    @Transactional
    public Optional<MemberAuth> removeMemberAuth(Long memberSeq, Long memberAuthDictionarySeq) {
        var optionalMemberAuth = memberAuthRepository.findById(new MemberAuthId(memberSeq, memberAuthDictionarySeq));
        if(optionalMemberAuth.isEmpty()){
            return Optional.empty();
        }
        memberAuthRepository.delete(optionalMemberAuth.get());
        return optionalMemberAuth;
    }
}
