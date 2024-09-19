package com.around.tdd.repository;

import com.around.tdd.vo.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() throws Exception {
        cartRepository.deleteAll();
    }

    @DisplayName("장바구니 저장 성공")
    @Test
    @Rollback(value =false)
    void saveProductCartItemSuccess(){
        //given
        Long memberSeq = 1L;
        Long productSeq = 1L;
        int productNum = 100;

        Cart cart = Cart.builder()
                .memberSeq(memberSeq)
                .productSeq(productSeq)
                .productNum(productNum)
                .build();

        //when
        Cart saveCart = cartRepository.save(cart);
        Optional<Cart> optionalCart = cartRepository.findById(saveCart.getCartSeq());

        //then
        assertThat(optionalCart.isPresent()).isTrue();
        assertThat(optionalCart.get().getProductSeq()).isEqualTo(productSeq);
        assertThat(optionalCart.get().getProductNum()).isEqualTo(productNum);
        assertThat(optionalCart.get().getMemberSeq()).isEqualTo(memberSeq);
    }

}
