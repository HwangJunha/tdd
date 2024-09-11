package com.around.tdd.controller;

import com.around.tdd.service.CartService;
import com.around.tdd.vo.Cart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @Operation(summary = "상품 장바구니 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "상품 장바구니 저장"),
            @ApiResponse(responseCode = "204",description = "상품 장바구니 저장할 데이터 없음")
    }
    )
    @PostMapping("/cart-add")
    public ResponseEntity<Cart> addItemToCart(@RequestBody @Valid Cart cart) {
        //cartService.saveProductCartItem(request);
        Map<String, Object> response = new HashMap<>();

        Cart saveCart = cartService.saveProductCartItem(cart);

        return ResponseEntity.ok(saveCart);
    }

}