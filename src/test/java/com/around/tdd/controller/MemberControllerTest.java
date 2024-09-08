package com.around.tdd.controller;

import com.around.tdd.config.EnableMockMvc;
import com.around.tdd.controller.response.MemberResponse;
import com.around.tdd.service.MemberService;
import com.around.tdd.vo.Member;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@EnableMockMvc //한글 깨짐 방지
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private final String baseUrl = "/api/v1/member";

    @Nested
    class MemberFindByIdHandler{
        private MemberResponse memberResponse;
        private Member member;
        private Long memberSeq;
        @BeforeEach
        public void setup(){
            this.memberSeq = 1L;
            this.member = Member.builder()
                    .id("tarot1415")
                    .password("!!1q2w3e4r")
                    .state(1)
                    .build();
            this.memberResponse = new MemberResponse(member);
        }

        @DisplayName("회원 조회")
        @Test
        void findByIdTest() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("memberSeq", String.valueOf(memberSeq));

            when(memberService.memberFindById(this.memberSeq)).thenReturn(Optional.of(member));
            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get(baseUrl+"/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isOk());
            verify(memberService).memberFindById(this.memberSeq);
        }

        @DisplayName("없는 회원 조회")
        @Test
        void findByIdEmptyTest() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("memberSeq", String.valueOf(0));

            when(memberService.memberFindById(0L)).thenReturn(Optional.empty());
            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isNoContent());
            verify(memberService).memberFindById(0L);
        }
    }
}