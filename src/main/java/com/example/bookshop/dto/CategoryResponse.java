package com.example.bookshop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    
    // Bắt buộc phải trả về ID để Frontend có thể lấy làm dữ liệu sửa/xóa 
    private Long id;

    private String name;
    
    private String description;
}