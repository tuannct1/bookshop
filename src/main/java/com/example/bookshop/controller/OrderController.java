package com.example.bookshop.controller;

import com.example.bookshop.dto.request.CheckoutRequestDTO;
import com.example.bookshop.dto.response.OrderResponseDTO;
import com.example.bookshop.enums.OrderStatus;
import com.example.bookshop.enums.PaymentStatus;
import com.example.bookshop.service.OrderService;
import com.example.bookshop.service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.bookshop.security.CustomUserDetails;
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final VNPayService vnPayService;
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(@Valid @RequestBody CheckoutRequestDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getId();       
        OrderResponseDTO response = orderService.checkout(currentUserId, requestDTO);     
        return new ResponseEntity<>(response, HttpStatus.CREATED); 
    }
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) OrderStatus status) { 
        Long userId = userDetails.getId();       
        List<OrderResponseDTO> response = orderService.getMyOrders(userId, status);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {
        Long userId = userDetails.getId();          
        OrderResponseDTO response = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {
        Long userId = userDetails.getId();            
        OrderResponseDTO response = orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<String> reOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {     
        Long userId = userDetails.getId();        
        orderService.reOrder(userId, orderId);
        return ResponseEntity.ok("Đã thêm các sản phẩm từ đơn hàng cũ vào giỏ hàng.");
    }
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponseDTO> confirmReceipt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {
        
        Long userId = userDetails.getId();      
        OrderResponseDTO response = orderService.confirmReceipt(userId, orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        boolean isValidSignature = vnPayService.verifyPayment(params);
        
        if (isValidSignature) {
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef"); 
            Long orderId = Long.parseLong(vnp_TxnRef);

            if ("00".equals(vnp_ResponseCode)) {
                orderService.updatePaymentStatus(orderId, PaymentStatus.PAID);
            } else {
                orderService.updatePaymentStatus(orderId, PaymentStatus.FAILED);
            }
        }
        
        String queryString = request.getQueryString();
        
        response.sendRedirect("http://localhost:5173/payment-result?" + queryString);
    }
}