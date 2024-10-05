package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.service.MemberService;
import com.around.tdd.service.SellerService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.Seller;
import com.around.tdd.vo.request.SellerRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
@Tag(name = "Seller", description = "판매자 관련 API 입니다.")
public class SellerController {

    private final SellerService sellerService;

    private final MemberService memberService;

    @PostMapping("/seller")
    public ResponseEntity<ApiResponse<Seller>> memberCreateHandler(
            @RequestBody SellerRequest sellerRequest
    ){

        var member = memberService.memberFindById(sellerRequest.memberSeq());
        if (member.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(Map.of(), "사용자 없음", HttpStatus.NO_CONTENT), HttpUtil.createJsonHeaders(), HttpStatus.NO_CONTENT);
        }
        var seller = sellerRequest.toSeller();
        seller.setMember(member.get());
        return new ResponseEntity<>(new ApiResponse<>(Map.of("seller", sellerService.createSeller(seller)), "판매자 등록 완료", HttpStatus.CREATED), HttpUtil.createJsonHeaders(), HttpStatus.CREATED);
    }
}
