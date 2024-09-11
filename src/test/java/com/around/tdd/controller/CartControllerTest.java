package com.around.tdd.controller;

import com.around.tdd.service.CartService;
import com.around.tdd.vo.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
        when(cartService.saveProductCartItem(any(Cart.class))).thenReturn(cart);
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
}