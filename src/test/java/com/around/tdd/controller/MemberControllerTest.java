package com.around.tdd.controller;

import com.around.tdd.config.EnableMockMvc;
import com.around.tdd.controller.response.MemberResponse;
import com.around.tdd.service.AuthService;
import com.around.tdd.service.MemberService;
import com.around.tdd.vo.AuthType;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @MockBean
    private AuthService authService;

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

    @Nested
    class MemberIdTest{
        @Test
        @DisplayName("아이디 조회 성공")
        void testGetMemberIdSuccess() throws Exception {
            //given
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            var memberSeq = 1L;
            var authType = AuthType.FIND_MEMBER;
            var authNumber = "123456";
            params.add("memberSeq", String.valueOf(memberSeq));
            params.add("authType", authType.name());
            params.add("authNumber", authNumber);

            var member = Member
                    .builder()
                    .id("junha1")
                    .build();

            //when
            when(authService.matchAuth(anyString(), eq(authNumber))).thenReturn(true);
            when(memberService.memberFindById(memberSeq)).thenReturn(Optional.of(member));


            mockMvc.perform(MockMvcRequestBuilders.get(baseUrl+"/member-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("조회 성공"))
                    .andExpect(jsonPath("$.data.id").value("junha1"));
        }

        @Test
        @DisplayName("사용자 없음 테스트")
        public void testGetMemberIdNoMember() throws Exception {
            Long memberSeq = 0L;
            String authNumber = "123456";
            var authType = AuthType.FIND_MEMBER;

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("memberSeq", String.valueOf(memberSeq));
            params.add("authType", authType.name());
            params.add("authNumber", authNumber);

            when(authService.matchAuth(anyString(), eq(authNumber))).thenReturn(true);
            when(memberService.memberFindById(memberSeq)).thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get(baseUrl+"/member-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$.message").value("사용자 없음"));
        }
    }

    @Nested
    class PasswordUpdateTest{

        private MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        private Long memberSeq = 1L;
        private String authNumber = "123456";
        private AuthType authType = AuthType.UPDATE_PASSWORD;
        private String password = "!!junha1234";

        @BeforeEach
        void setUp(){
            params.add("memberSeq", String.valueOf(memberSeq));
            params.add("authType", authType.name());
            params.add("password", password);
            params.add("authNumber", authNumber);
        }

        @Test
        @DisplayName("패스워드 변경 성공 테스트")
        void testPutMemberPasswordSuccessChange() throws Exception {
            //when
            when(authService.matchAuth(anyString(), eq(authNumber))).thenReturn(true);
            when(memberService.checkPassword(eq(password))).thenReturn(true);
            when(memberService.changePassword(eq(memberSeq), eq(password))).thenReturn(true);

            // then
            mockMvc.perform(MockMvcRequestBuilders.put(baseUrl+"/member-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isChanged").value("true"))
                    .andExpect(jsonPath("$.message").value("비밀번호 변경 완료"));

        }

        @Test
        @DisplayName("인증번호 틀림 테스트")
        void testPutMemberPasswordAuthNumberMismatch() throws Exception {

            // When
            when(authService.matchAuth(anyString(), eq(authNumber))).thenReturn(false);

            mockMvc.perform(MockMvcRequestBuilders.put(baseUrl+"/member-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("인증번호 맞지 않음"));
        }

        @Test
        @DisplayName("패스워드 형식이 올바르지 않음")
        void testPutMemberPasswordInvalidPasswordFormat() throws Exception {
            // Given
            this.password = "123456";  // 비밀번호 형식이 올바르지 않음
            params.add("password", password);

            // Mocking
            when(authService.matchAuth(anyString(), eq(authNumber))).thenReturn(true);
            when(memberService.checkPassword(eq(password))).thenReturn(false);

            // When
            mockMvc.perform(MockMvcRequestBuilders.put(baseUrl+"/member-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.message").value("패스워드 형식이 맞지 않음"));
        }
    }

}