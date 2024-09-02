package com.around.tdd.controller;

import com.around.tdd.service.AuthService;
import com.around.tdd.service.EmailSendService;
import com.around.tdd.service.MemberService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.MailDto;
import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> authNumber(@RequestBody MemberRequest memberRequest) {
        Optional<Member> optionalMember = memberService.memberFindById(memberRequest.getMemberSeq());
        if(!optionalMember.isPresent()) {
            return new ResponseEntity<>("사용자 정보가 없음",HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        Member member = optionalMember.get();
        String authNumber = authService.getAuthNumber();
        authService.saveAuth("auth-member:"+memberRequest.getMemberSeq(), authNumber);
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
    public ResponseEntity<String> authCheck(@RequestBody MemberRequest memberRequest) {
        boolean authCheck = authService.matchAuth("auth-member:"+memberRequest.getMemberSeq(), memberRequest.getAuthNumber());
        if(!authCheck){
            return new ResponseEntity<>(String.valueOf(false),HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }

        String token = authService.getToken(memberRequest.getMemberSeq().intValue());

        authService.saveAuth("auth-token:"+memberRequest.getMemberSeq(), token);
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
            @RequestParam Long memberSeq,
            @RequestParam String authToken
    ) {
        boolean authCheck = authService.matchAuth("auth-token:"+memberSeq, authToken);
        return new ResponseEntity<>(String.valueOf(authCheck), HttpUtil.createJsonHeaders(), authCheck ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }
}
