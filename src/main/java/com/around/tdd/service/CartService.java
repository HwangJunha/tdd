package com.around.tdd.service;

import com.around.tdd.repository.CartRepository;
import com.around.tdd.vo.Cart;
import com.around.tdd.vo.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Cart> getCartList(Long memberSeq) {
        if (memberSeq == null) {
            throw new IllegalArgumentException("MemberSeq cannot be null or empty");
        }
        return cartRepository.findByMemberSeq(memberSeq);
    }
    @Transactional
    public Cart updateCart(Cart cart) {
        if (cart == null || cart.getMemberSeq() == null) {
            throw new IllegalArgumentException("Cart or MemberSeq cannot be null or empty");
        }

        List<Cart> existingCarts = cartRepository.findByMemberSeq(cart.getMemberSeq());
        if (existingCarts.isEmpty()) {
            throw new IllegalArgumentException("No cart found for the given MemberSeq");
        }

        Cart existingCart = existingCarts.get(0);
        existingCart.setProductSeq(cart.getProductSeq());
        existingCart.setProductNum(cart.getProductNum());

        return cartRepository.save(existingCart);
    }
}
