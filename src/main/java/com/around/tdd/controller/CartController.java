package com.around.tdd.controller;

import com.around.tdd.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> addItemToCart(@RequestBody CartItemRequest request) {
        cartService.saveProductItem(request);
        return ResponseEntity.ok("Item added to cart");
    }

}
