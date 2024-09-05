package com.around.tdd.controller;

import com.around.tdd.config.EnableMockMvc;
import com.around.tdd.service.AuthService;
import com.around.tdd.service.EmailSendService;
import com.around.tdd.service.MemberService;
import com.around.tdd.vo.MailDto;
import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberInfo;
import com.around.tdd.vo.MemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@EnableMockMvc //한글 깨짐 방지
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private EmailSendService emailSendService;

    @MockBean
    private MemberService memberService;

    private final String baseUrl = "/api/v1/auth";

    @Test
    @DisplayName("인증번호 저장 및 메일 발송 성공 테스트")
    void authNumberSaveAndEmailSendTest() throws Exception {
        // given
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberSeq(1L);
        Member member = new Member();
        member.setMemberSeq(1L);
        String content = objectMapper.writeValueAsString(memberRequest);

        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setEmail("tarot1415@gmail.com");
        member.setMemberInfo(memberInfo);
        when(memberService.memberFindById(1L)).thenReturn(Optional.of(member));
        when(authService.getAuthNumber()).thenReturn("123456");
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl+"/auth-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("인증번호 저장 성공"));
        //then
        verify(authService).saveAuth(eq("auth-member:1"), eq("123456"));
        verify(emailSendService).sendSimpleMessage(any(MailDto.class));
    }

    @Test
    @DisplayName("사용자 정보가 없을 때 204 응답")
    void authNumberMemberNotFound() throws Exception {
        // Given
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberSeq(1L);
        String content = objectMapper.writeValueAsString(memberRequest);
        Mockito.when(memberService.memberFindById(99999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl+"/auth-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string("사용자 정보가 없음"));

        // Then
        verify(authService, never()).getAuthNumber();
        verify(authService, never()).saveAuth(any(), any());
        verify(emailSendService, never()).sendSimpleMessage(any(MailDto.class));
    }

    @Test
    @DisplayName("인증번호가 일치했을 토큰값 반환 테스트")
    void authCheckSuccessTokenSaveTest() throws Exception {
        //given
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberSeq(1L);
        memberRequest.setAuthNumber("123456");
        String content = objectMapper.writeValueAsString(memberRequest);
        String redisKey = "auth-member:"+memberRequest.getMemberSeq();
        String token = "1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014";


        when(authService.getToken(memberRequest.getMemberSeq().intValue())).thenReturn(token);
        when(authService.matchAuth(redisKey, memberRequest.getAuthNumber())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl+"/auth-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014"));
        verify(authService).saveAuth(eq("auth-token:"+memberRequest.getMemberSeq()), eq(token));
    }

    @Test
    @DisplayName("인증번호가 일치하지 않았을때 테스트")
    void authCheckFailTokenTest() throws Exception {
        //given
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberSeq(1L);
        memberRequest.setAuthNumber("123456");
        String content = objectMapper.writeValueAsString(memberRequest);
        String redisKey = "auth-member:"+memberRequest.getMemberSeq();

        when(authService.matchAuth(redisKey, memberRequest.getAuthNumber())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl+"/auth-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string("false"));
        verify(authService, never()).saveAuth(redisKey, memberRequest.getAuthNumber());
    }

    @Test
    @DisplayName("인증 토큰 일치/불일치 테스트")
    void authTokenTest() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("memberSeq", "1");
        params.add("authToken", "1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014");

        String redisKey = "auth-token:1";
        when(authService.matchAuth(redisKey, "1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014")).thenReturn(true);
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl+"/auth-token-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(params)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        params.add("authToken", "60303ae22b998861bce3b28f33eec1be758a213c86c93c076dbe9f558c11c752");
        when(authService.matchAuth(redisKey, "60303ae22b998861bce3b28f33eec1be758a213c86c93c076dbe9f558c11c752")).thenReturn(false);
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl+"/auth-token-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(params)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string("false"));
    }
}