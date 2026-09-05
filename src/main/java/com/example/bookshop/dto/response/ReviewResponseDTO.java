package com.example.bookshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private String userName; 
    private LocalDateTime createdAt;
}