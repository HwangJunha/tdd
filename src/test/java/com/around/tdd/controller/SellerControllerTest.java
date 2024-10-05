package com.around.tdd.controller;

import com.around.tdd.config.EnableMockMvc;
import com.around.tdd.service.MemberService;
import com.around.tdd.service.SellerService;
import com.around.tdd.vo.Member;
import com.around.tdd.vo.request.SellerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@EnableMockMvc //한글 깨짐 방지
@WebMvcTest(controllers = SellerController.class)
class SellerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private SellerService sellerService;


    private final String baseUrl = "/api/v1/seller";

    @Nested
    class InsertSellerTest{

        private SellerRequest sellerRequest;

        private Member member;

        @BeforeEach
        void setUp() {
            this.sellerRequest = new SellerRequest(
                    0L,
                    1L,
                    "01084282511",
                        "준하",
                        "신림",
                    "1234",
                    "123456",
                    0,
                    0
                    );
            this.member = Member.builder().build();
        }

        @Test
        @DisplayName("판매자 등록")
        void testMemberCreateHandler_Success() throws Exception {
            // given
            var seller = sellerRequest.toSeller();

            // mocking memberService와 sellerService의 동작
            when(memberService.memberFindById(sellerRequest.memberSeq())).thenReturn(Optional.of(member));
            when(sellerService.createSeller(any())).thenReturn(seller);

            String content = objectMapper.writeValueAsString(sellerRequest);

            mockMvc.perform(MockMvcRequestBuilders.post(baseUrl+"/seller")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                    )
                    .andExpect(status().isCreated()) // 상태 코드 201 CREATED 확인
                    .andExpect(jsonPath("$.message").value("판매자 등록 완료")) // 응답 메시지 확인
                    .andExpect(jsonPath("$.data.seller.sellerSeq").value(seller.getSellerSeq())); // 반환된 seller의 ID 확인

        }

        @Test
        @DisplayName("사용자 확인 ")
        void testMemberCreateHandler_MemberNotFound() throws Exception {
            // given
            Long memberSeq = 1L;
            String content = objectMapper.writeValueAsString(sellerRequest);
            // mocking memberService에서 빈 값을 반환하도록 설정
            when(memberService.memberFindById(memberSeq)).thenReturn(Optional.empty());

            // when & then
            mockMvc.perform(MockMvcRequestBuilders.post(baseUrl+"/seller")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                    )
                    .andExpect(status().isNoContent()) // 상태 코드 204 NO CONTENT 확인
                    .andExpect(jsonPath("$.message").value("사용자 없음")); // 응답 메시지 확인
        }
    }


}