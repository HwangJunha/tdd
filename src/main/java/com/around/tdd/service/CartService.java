package com.around.tdd.service;

import com.around.tdd.repository.CartRepository;
import com.around.tdd.vo.Cart;
import com.around.tdd.vo.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {


    private final CartRepository cartRepository;

    @Transactional
    public Cart saveCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null or empty");
        }
        return cartRepository.save(cart);
    }

    public Cart getCartList(Long memberSeq) {
        if(memberSeq == null) {
            throw new IllegalArgumentException("MemberSeq cannot be null or empty");
        }
        return cartRepository.findById(memberSeq).orElse(null);
    }
}
