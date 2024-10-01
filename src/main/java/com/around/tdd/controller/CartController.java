package com.around.tdd.controller;

import com.around.tdd.service.CartService;
import com.around.tdd.vo.Cart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

        Cart saveCart = cartService.saveCart(cart);

        return ResponseEntity.ok(saveCart);
    }

    @GetMapping("/cart-list")
    public ResponseEntity<Map<String, Object>> getCartList(@RequestParam Long memberSeq) {
        if (memberSeq == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Cart> cartList = cartService.getCartList(memberSeq);
        if (cartList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cartList", cartList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cart-update")
    public ResponseEntity<Cart> updateCartItem(@RequestBody @Valid Cart cart) {
        if (cart.getCartSeq() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Cart updatedCart = cartService.updateCart(cart);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/cart-delete/{cartSeq}")
    public ResponseEntity<Map<String, Object>> deleteCartItem(@PathVariable Long cartSeq) {
        if (cartSeq == null) {
            return ResponseEntity.badRequest().body(null);
        }

        cartService.deleteCart(cartSeq);
        return ResponseEntity.ok().build();
    }

}