package com.around.tdd.service;

import com.around.tdd.repository.CartRepository;
import com.around.tdd.vo.Cart;
import com.around.tdd.vo.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {


    private final CartRepository cartRepository;

    public Cart saveProductCartItem(Cart cart) {

        return cartRepository.save(cart);
    }
}
