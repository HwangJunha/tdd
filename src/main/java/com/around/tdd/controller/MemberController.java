package com.around.tdd.controller;

import com.around.tdd.controller.response.MemberResponse;
import com.around.tdd.service.MemberService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberInfo;
import com.around.tdd.vo.request.MemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "Member 관련 API 입니다.")
public class MemberController {

    private final MemberService memberService;

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
        return new ResponseEntity<>(new MemberResponse(member.get()),HttpUtil.createJsonHeaders(), HttpStatus.OK);
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
        return new ResponseEntity<>(new MemberResponse(memberService.insertMemberInfo(member)),HttpUtil.createJsonHeaders(), HttpStatus.OK);
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
        return new ResponseEntity<>(new MemberResponse(savedMember), HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
    }


}
