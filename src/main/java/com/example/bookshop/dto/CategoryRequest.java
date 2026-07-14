package com.example.bookshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 3, max = 50, message = "Tên danh mục phải từ 3 đến 50 ký tự")
    private String name;

    @Size(max = 200, message = "Mô tả danh mục không được vượt quá 200 ký tự")
    private String description;
}