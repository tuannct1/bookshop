package com.example.bookshop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.bookshop.security.CustomUserDetails;
import com.example.bookshop.dto.request.CartItemRequest;
import com.example.bookshop.dto.response.CartItemResponse;
import com.example.bookshop.service.CartItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartItemResponse> createCartItem(@RequestBody CartItemRequest cartItemRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getId();
        CartItemResponse response = cartItemService.createCartItem(currentUserId, cartItemRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-cart") 
    public ResponseEntity<Page<CartItemResponse>> getAllCartItems(
            @AuthenticationPrincipal CustomUserDetails userDetails, 
            Pageable pageable) {
        Long currentUserId = userDetails.getId(); 
        Page<CartItemResponse> responses = cartItemService.getAllCartItem(currentUserId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponse> getCartItem(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartItemResponse response = cartItemService.getCartItem(userDetails.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long id, 
            @RequestBody CartItemRequest cartItemRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartItemResponse response = cartItemService.updateCartItem(userDetails.getId(), id, cartItemRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartItemService.deleteCartItem(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}