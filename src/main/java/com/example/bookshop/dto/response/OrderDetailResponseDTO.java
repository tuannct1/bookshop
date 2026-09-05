package com.example.bookshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderDetailResponseDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;  
    private String imageUrl;   
    private int quantity;
    private double price;
    private double subtotal;
}