package com.example.bookshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BookRequest {
    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Mô tả sách không được để trống")
    private String description;

    private String imageUrl; // Thường trường này có thể để trống lúc tạo, update sau nên không bắt buộc

    @NotNull(message = "Giá tiền không được để trống")
    @Min(value = 0, message = "Giá tiền không được nhỏ hơn 0")
    private Double price;

    @NotNull(message = "Số lượng không được để trống")
    @PositiveOrZero(message = "Số lượng sản phẩm phải từ 0 trở lên")
    private Integer quantity; // Nên đổi sang kiểu Integer để có thể check @NotNull khi client quên truyền

    @NotNull(message = "Năm xuất bản không được để trống")
    @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
    private Integer publishedYear; // Nên đổi từ int sang Integer để bắt được lỗi nếu client bỏ trống
    
    @NotNull(message = "Trang thái khong được để trống")
    @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
    private String status; 

    @NotNull(message = "Danh mục sách không được để trống")
    private Long categoryId;
}
