package com.around.tdd.controller;

import com.around.tdd.controller.response.MemberResponse;
import com.around.tdd.service.AuthService;
import com.around.tdd.service.MemberService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.AuthType;
import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberInfo;
import com.around.tdd.vo.request.MemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "Member 관련 API 입니다.")
public class MemberController {

    private final HttpHeaders JSON_HEADERS = HttpUtil.createJsonHeaders();

    private final MemberService memberService;

    private final AuthService authService;

    @Operation(
            summary = "member 조회 함수"
    )
    @ApiResponses(value = {
            @ApiResponse( responseCode = "200", description = "정상 호출"),
            @ApiResponse( responseCode = "204", description = "사용자 없음")
    }
    )
    @GetMapping("/member")
    public ResponseEntity<MemberResponse> memberFindByIdHandler(
            @RequestParam(value ="memberSeq") Long memberSeq
    ) {
        var member = memberService.memberFindById(memberSeq);
        if(member.isEmpty()){
            return new ResponseEntity<>(HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new MemberResponse(member.get()),JSON_HEADERS, HttpStatus.OK);
    }

    @PostMapping("/member")
    public ResponseEntity<MemberResponse> memberCreateHandler(
            @RequestBody MemberRequest memberRequest
    ){
        var validation = this.memberService.checkUserInput(memberRequest.id(), memberRequest.email(), memberRequest.password());
        if(!validation.isValid()){
            return new ResponseEntity<>(HttpUtil.createJsonHeaders(), HttpStatus.ACCEPTED);
        }
        Member member = memberRequest.fromMember();
        MemberInfo memberInfo = memberRequest.fromMemberInfo();
        memberInfo.setMember(member);
        member.setMemberInfo(memberInfo);
        return new ResponseEntity<>(new MemberResponse(memberService.insertMemberInfo(member)),JSON_HEADERS, HttpStatus.OK);
    }

    @PostMapping("/member-delivery-info")
    public ResponseEntity<MemberResponse> memberDeliveryInfoCreateHandler(
            @RequestBody MemberRequest memberRequest
    ){
        var optionalMember = memberService.memberFindById(memberRequest.memberSeq());
        if(optionalMember.isEmpty()){
            return new ResponseEntity<>(HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        var member = optionalMember.get();
        member.setMemberDeliveryInfo(memberRequest.fromMemberDeliveryInfo());
        member.getMemberDeliveryInfo().forEach(memberDeliveryInfo -> memberDeliveryInfo.setMember(member));
        var savedMember = memberService.insertMemberInfo(member);
        return new ResponseEntity<>(new MemberResponse(savedMember), JSON_HEADERS, HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "조회 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 완료"),
            @ApiResponse(responseCode = "204", description = "인증 없음")
    })
    @GetMapping("/member-id")
    public ResponseEntity<com.around.tdd.controller.response.ApiResponse<String>> getMemberId(
            @RequestParam(value="memberSeq") Long memberSeq,
            @RequestParam(value="authNumber") String authNumber,
            @RequestParam(value="authType") AuthType authType) {
        boolean authCheck = authService.matchAuth(AuthType.AUTH_TYPE_MAP.get(authType)+":"+ memberSeq, authNumber);
        if(!authCheck){
            return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of(), "인증번호 맞지 않음", HttpStatus.NO_CONTENT), HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        var optionalMember = memberService.memberFindById(memberSeq);
        return optionalMember.map(member -> new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of("id", member.getId()), "조회 성공", HttpStatus.OK), HttpUtil.createJsonHeaders(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of(), "사용자 없음", HttpStatus.NO_CONTENT), HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT));
    }

    @Operation(
            summary = "수정 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "204", description = "인증 없음")
    })
    @PutMapping("/member-password")
    public ResponseEntity<com.around.tdd.controller.response.ApiResponse<String>> putMemberPassword(
            @RequestParam(value="memberSeq") Long memberSeq,
            @RequestParam(value="authNumber") String authNumber,
            @RequestParam(value="authType") AuthType authType,
            @RequestParam(value="password") String password) {
        boolean authCheck = authService.matchAuth(AuthType.AUTH_TYPE_MAP.get(authType)+":"+ memberSeq, authNumber);
        if(!authCheck){
            return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of(), "인증번호 맞지 않음", HttpStatus.UNAUTHORIZED), HttpUtil.createJsonHeaders(), HttpStatus.UNAUTHORIZED);
        }
        boolean checkPassword = memberService.checkPassword(password);
        if(!checkPassword){
            return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of(), "패스워드 형식이 맞지 않음", HttpStatus.ACCEPTED), HttpUtil.createJsonHeaders(), HttpStatus.ACCEPTED);
        }

        var isChanged = memberService.changePassword(memberSeq, password);
        return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of("isChanged", String.valueOf(isChanged)), "비밀번호 변경 완료", HttpStatus.OK), HttpUtil.createJsonHeaders(), HttpStatus.OK);
    }


    @Operation(
            summary = "수정 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "204", description = "인증 없음")
    })
    @PutMapping("/member-info")
    public ResponseEntity<com.around.tdd.controller.response.ApiResponse<MemberInfo>> putMemberInfo(
            @RequestParam(value="memberSeq") Long memberSeq,
            @RequestParam(value="authNumber") String authNumber,
            @RequestParam(value="authType") AuthType authType,
            @RequestParam(value="name", required = false) String name,
            @RequestParam(value="phone", required = false) String phone,
            @RequestParam(value="email", required = false) String email,
            @RequestParam(value="nick", required = false) String nick,
            @RequestParam(value="address", required = false) String address,
            @RequestParam(value="detailAddress", required = false) String detailAddress,
            @RequestParam(value="post", required = false) String post
            ) {
        boolean authCheck = authService.matchAuth(AuthType.AUTH_TYPE_MAP.get(authType)+":"+ memberSeq, authNumber);
        if(!authCheck){
            return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of(), "인증번호 맞지 않음", HttpStatus.UNAUTHORIZED), HttpUtil.createJsonHeaders(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(new com.around.tdd.controller.response.ApiResponse<>(Map.of("memberInfo", memberService.changeMemberInfo(memberSeq,name, phone, email, nick, address, detailAddress, post)), "회원 수정 완료", HttpStatus.OK), HttpUtil.createJsonHeaders(), HttpStatus.OK);
    }

}
