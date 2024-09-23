package com.around.tdd.controller;

import com.around.tdd.service.AuthService;
import com.around.tdd.service.EmailSendService;
import com.around.tdd.service.MemberService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.*;
import com.around.tdd.vo.request.AuthRequest;
import com.around.tdd.vo.request.MemberAuthDictionaryRequest;
import com.around.tdd.vo.request.MemberAuthRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth 관련 API 입니다.")
public class AuthController {

    private final AuthService authService;

    private final EmailSendService emailSendService;

    private final MemberService memberService;

    @Operation(
            summary = "인증번호 생성 및 이메일 발송"
    )
    @ApiResponses(value = {
            @ApiResponse( responseCode = "201", description = "인증번호 생성 후 저장됨"),
            @ApiResponse( responseCode = "204", description = "해당 memberSeq는 사용자가 존재 하지 않음")
        }
    )
    @PostMapping("/auth-number")
    public ResponseEntity<String> authNumber(@RequestBody AuthRequest authRequest) {
        Optional<Member> optionalMember = memberService.memberFindById(authRequest.getMemberSeq());
        if(!optionalMember.isPresent()) {
            return new ResponseEntity<>("사용자 정보가 없음",HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        Member member = optionalMember.get();
        String authNumber = authService.getAuthNumber();
        authService.saveAuth("auth-member:"+ authRequest.getMemberSeq(), authNumber);
        emailSendService.sendSimpleMessage(new MailDto("tdd@gmail.com", member.getMemberInfo().getEmail(), "인증번호", "인증번호:"+authNumber));
        return new ResponseEntity<>("인증번호 저장 성공",HttpUtil.createJsonHeaders(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "인증번호 확인 메소드"
    )
    @ApiResponses(value = {
            @ApiResponse( responseCode = "201", description = "인증 토큰 생성 후 저장 및 반환"),
            @ApiResponse( responseCode = "204", description = "인증번호가 일치 하는 사용자가 없음")
        }
    )
    @PostMapping("/auth-check")
    public ResponseEntity<String> authCheck(@RequestBody AuthRequest authRequest) {
        boolean authCheck = authService.matchAuth("auth-member:"+ authRequest.getMemberSeq(), authRequest.getAuthNumber());
        if(!authCheck){
            return new ResponseEntity<>(String.valueOf(false),HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }

        String token = authService.getToken(authRequest.getMemberSeq().intValue());

        authService.saveAuth("auth-token:"+ authRequest.getMemberSeq(), token);
        return new ResponseEntity<>(token,HttpUtil.createJsonHeaders(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "인증토큰 확인 메소드"
    )
    @ApiResponses(value = {
            @ApiResponse( responseCode = "200", description = "인증 토큰 일치"),
            @ApiResponse( responseCode = "204", description = "인증토큰이 일치 하는 사용자가 없음")
    }
    )
    @GetMapping("/auth-token-check")
    public ResponseEntity<String> authTokenCheck(
            @RequestParam(value="memberSeq") Long memberSeq,
            @RequestParam(value="authToken") String authToken
    ) {
        boolean authCheck = authService.matchAuth("auth-token:"+memberSeq, authToken);
        return new ResponseEntity<>(String.valueOf(authCheck), HttpUtil.createJsonHeaders(), authCheck ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "회원 권한 입력 api"
    )
    @ApiResponses(value = {
            @ApiResponse( responseCode = "201", description = "신규 권한 등록 성공"),
    }
    )
    @PostMapping("/member-auth-dictionary")
    public ResponseEntity<com.around.tdd.controller.response.ApiResponse<MemberAuthDictionary>>
        insertMemberAuthDictionary(@RequestBody MemberAuthDictionaryRequest memberAuthDictionaryRequest) {

        MemberAuthDictionary memberAuthDictionary = authService.insertMemberAuthDictionary(memberAuthDictionaryRequest.fromMemberAuthDictionary());
        return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of("memberAuthDictionary", memberAuthDictionary), "신규 권한 등록 성공", HttpStatus.CREATED),HttpUtil.createJsonHeaders(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "회원 권한 매치 api"
    )
    @ApiResponses(value = {
            @ApiResponse( responseCode = "201", description = "신규 권한 매치 성공"),
    }
    )
    @PostMapping("/member-auth")
    public ResponseEntity<com.around.tdd.controller.response.ApiResponse<MemberAuth>>
    insertMemberAuth(@RequestBody MemberAuthRequest memberAuthRequest) {
        var memberAuth = memberAuthRequest.fromMemberAuth();
        return authService.matchMemberAuthDictionary(memberAuth);
    }

    @Operation(
            summary = "권한 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "권한 있음"),
            @ApiResponse(responseCode = "204", description = "권한 없음")
    })
    @GetMapping("/member-auth-check")
    public ResponseEntity<com.around.tdd.controller.response.ApiResponse<Boolean>> checkMemberAuth(
            @RequestParam(value="memberSeq") Long memberSeq,
            @RequestParam(value="memberAuthDictionarySeq") Long memberAuthDictionarySeq) {
        Boolean result = authService.checkMemberAuth(memberSeq, memberAuthDictionarySeq);
        if(!result){
            return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of(), "권한 없음", HttpStatus.NO_CONTENT),HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of("result", result), "권한 있음", HttpStatus.OK), HttpUtil.createJsonHeaders(), HttpStatus.OK);
    }

    @Operation(
            summary = "권한 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "권한 삭제 완료"),
            @ApiResponse(responseCode = "204", description = "권한 없음")
    })
    @DeleteMapping("/member-auth")
    public ResponseEntity<com.around.tdd.controller.response.ApiResponse<MemberAuth>> removeMemberAuth(
            @RequestParam(value="memberSeq") Long memberSeq,
            @RequestParam(value="memberAuthDictionarySeq") Long memberAuthDictionarySeq) {
        var optionalMemberAuth = authService.removeMemberAuth(memberSeq, memberAuthDictionarySeq);
        return optionalMemberAuth.map(memberAuth -> new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of("memberAuth", memberAuth), "권한 삭제 완료", HttpStatus.OK), HttpUtil.createJsonHeaders(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of(), "권한 없음", HttpStatus.NO_CONTENT), HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT));
    }
}
