package com.example.bookshop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    // 1. Thêm ID - Bắt buộc phải có ở chiều trả về
    private Long id;

    private String title;
    private String description;
    private String imageUrl;
    private Double price;
    private Integer quantity; // Đồng bộ sang Integer cho sạch đẹp
    private Integer publishedYear;

    // 2. Thêm trạng thái để hiển thị badge trên giao diện
    private String status; 

    // 3. Trả thêm tên danh mục để hiển thị (Ví dụ: "Thể loại: Truyện tranh")
    private String categoryName; 
}
