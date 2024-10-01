package com.around.tdd.service;

import com.around.tdd.repository.CartRepository;
import com.around.tdd.vo.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartServiceTest {

    @InjectMocks
    private CartService cartService;  // 실제 테스트할 서비스

    @Mock
    private CartRepository cartRepository;  // 모킹할 리포지토리

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);  // Mockito 애노테이션 초기화
    }

    @Test
    @DisplayName("장바구니 저장 성공 테스트")
    public void saveCartSuccess() {
        // given 장바구니 데이터 설정
        Cart cart = new Cart();
        cart.setCartSeq(1L);
        cart.setMemberSeq(1L);
        cart.setProductSeq(100L);
        cart.setProductNum(2);

        // cartRepository.save() 호출 시 저장된 Cart 객체를 반환하
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // when - CartService의 saveCart 호출
        Cart savedCart = cartService.saveCart(cart);

        // then - 결과 확인
        assertEquals(1L, savedCart.getCartSeq());
        assertEquals(1L, savedCart.getMemberSeq());
        assertEquals(100L, savedCart.getProductSeq());
        assertEquals(2, savedCart.getProductNum());

        // cartRepository.save() 메서드 호출확인
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 저장 실패 테스트")
    public void saveCartFail() {
        // given 장바구니 데이터 설정
        Cart cart = new Cart();
        cart.setCartSeq(1L);
        cart.setMemberSeq(1L);
        cart.setProductSeq(100L);
        cart.setProductNum(2);

        // cartRepository.save() 호출 시 저장된 Cart 객체를 반환하
        when(cartRepository.save(any(Cart.class))).thenReturn(null);

        // when - CartService의 saveCart 호출
        Cart savedCart = cartService.saveCart(cart);

        // then - 결과 확인
        assertEquals(null, savedCart);

        // cartRepository.save() 메서드 호출확인
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 수정 성공 테스트")
    public void updateCartSuccess() {
        // given 장바구니 데이터 설정
        Cart cart = new Cart();
        cart.setCartSeq(1L);
        cart.setMemberSeq(1L);
        cart.setProductSeq(100L);
        cart.setProductNum(2);

        // cartRepository.findByMemberSeq() 호출 시 저장된 Cart 객체를 반환
        when(cartRepository.findByMemberSeq(1L)).thenReturn((List<Cart>) cart);

        // when - CartService의 updateCart 호출
        Cart updatedCart = cartService.updateCart(cart);

        // then - 결과 확인
        assertEquals(1L, updatedCart.getCartSeq());
        assertEquals(1L, updatedCart.getMemberSeq());
        assertEquals(100L, updatedCart.getProductSeq());
        assertEquals(2, updatedCart.getProductNum());

        // cartRepository.findByMemberSeq() 메서드 호출확인
        verify(cartRepository).findByMemberSeq(1L);
    }

    @Test
    @DisplayName("장바구니 수정 실패 테스트")
    public void updateCartFail() {
        // given 장바구니 데이터 설정
        Cart cart = new Cart();
        cart.setCartSeq(1L);
        cart.setMemberSeq(1L);
        cart.setProductSeq(100L);
        cart.setProductNum(2);

        // cartRepository.findByMemberSeq() 호출 시 빈 리스트 반환
        when(cartRepository.findByMemberSeq(1L)).thenReturn(Collections.emptyList());

        // when - CartService의 updateCart 호출 및 예외 발생 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.updateCart(cart);
        });

        // then - 예외 메시지 확인
        assertEquals("회원번호가 없어 카트가 비어있음", exception.getMessage());

        // cartRepository.findByMemberSeq() 메서드 호출확인
        verify(cartRepository).findByMemberSeq(1L);
    }
}