package com.example.bookshop.dto.response;

import com.example.bookshop.enums.OrderStatus;
import com.example.bookshop.enums.PaymentMethod;
import com.example.bookshop.enums.PaymentStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private Long id;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
    private double totalPrice;
    private String note;
    private LocalDateTime createdAt; 
    private String paymentUrl;
    private List<OrderDetailResponseDTO> orderDetails;
}