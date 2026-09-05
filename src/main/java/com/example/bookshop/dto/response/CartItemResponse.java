package com.example.bookshop.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private int  quantity;
    private Long userId;  
    private String  bookName;
    private Long bookId;      
    private double price;     
    private String imageUrl;  
}
