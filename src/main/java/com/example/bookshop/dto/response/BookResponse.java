package com.example.bookshop.dto.response;

import java.util.List;
import java.util.Set;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;

    private String title;
    private String description;
    private String imageUrl;
    private Double price;
    private Integer quantity; 
    private Integer publishedYear;

    private String status; 

    private String categoryName; 
    private List<String> authorNames; 
    private String publisherName; 
}
