package com.example.bookshop.dto.response;

import com.example.bookshop.enums.ReturnStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReturnResponseDTO {
    
    private Long id;
    
    private Long orderId; 
    
    private String reason;
    private String proofImages;
    private Double refundAmount;
    private ReturnStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}