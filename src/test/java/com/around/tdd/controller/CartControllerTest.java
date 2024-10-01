package com.around.tdd.controller;

import com.around.tdd.service.CartService;
import com.around.tdd.vo.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CartController.class)
@ActiveProfiles("test")
@Import(CartController.class)  // ObjectMapper를 명시적으로 추가
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper 자동 주입

    @MockBean
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private final String baseUrl = "/api/v1/cart";

    @Test
    @DisplayName("장바구니 저장 테스트")
    public void saveCartProductTest() throws Exception {
        // given
        Cart cart = new Cart();
        cart.setCartSeq(1L);
        cart.setMemberSeq(1L);
        cart.setProductSeq(1L);
        cart.setProductNum(1);

        // when
        when(cartService.saveCart(any(Cart.class))).thenReturn(cart);
        // the
        mockMvc.perform(post(baseUrl + "/cart-add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cart)))  // ObjectMapper로 JSON 변환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartSeq").value(1L))
                .andExpect(jsonPath("$.memberSeq").value(1L))
                .andExpect(jsonPath("$.productSeq").value(1L))
                .andExpect(jsonPath("$.productNum").value(1));
    }

    @Test
    @DisplayName("장바구니 저장 실패 테스트 - null 또는 Empty")
    public void saveCartProductFailTest() throws Exception {
        // given
        Cart emptyCart = new Cart();

        // when
        when(cartService.saveCart(any(Cart.class))).thenThrow(new IllegalArgumentException("장바구니 저장 실패"));

        // then
        mockMvc.perform(post(baseUrl + "/cart-add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCart)))  // ObjectMapper로 JSON 변환
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("장바구니 저장 실패"));
    }

    @Test
    @DisplayName("장바구니 조회 성공 테스트")
    void getCartListSuccessTest() throws Exception {
        // given
        Long memberSeq = 1L;
        Cart cart =new Cart();
        cart.setCartSeq(1L);   // 장바구니 번호
        cart.setMemberSeq(memberSeq);  // 회원 번호
        cart.setProductSeq(1L);  // 상품 번호
        cart.setProductNum(1);  // 상품 수량

        List<Cart> cartList = Collections.singletonList(cart);
        when(cartService.getCartList(memberSeq)).thenReturn(null);
        // when
        ResponseEntity<Map<String,Object>> response = cartController.getCartList(memberSeq);


        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, ((List<?>) response.getBody().get("cartList")).size());
    }

    @Test
    @DisplayName("장바구니 수정 성공 테스트")
    void updateCartSuccessTest() throws Exception {
        // given
        Cart cart = new Cart();
        cart.setCartSeq(1L);
        cart.setMemberSeq(1L);
        cart.setProductSeq(1L);
        cart.setProductNum(1);

        // when
        when(cartService.updateCart(any(Cart.class))).thenReturn(cart);

        // then
        mockMvc.perform(post(baseUrl + "/cart-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartSeq").value(1L))
                .andExpect(jsonPath("$.memberSeq").value(1L))
                .andExpect(jsonPath("$.productSeq").value(1L))
                .andExpect(jsonPath("$.productNum").value(1));
    }

}