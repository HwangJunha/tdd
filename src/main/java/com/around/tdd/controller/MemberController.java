package com.around.tdd.controller;

import com.around.tdd.controller.response.MemberResponse;
import com.around.tdd.service.MemberService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
        Optional<Member> member = memberService.memberFindById(memberSeq);
        if(member.isEmpty()){
            return new ResponseEntity<>(HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new MemberResponse(member.get()),HttpUtil.createJsonHeaders(), HttpStatus.OK);
    }


}
