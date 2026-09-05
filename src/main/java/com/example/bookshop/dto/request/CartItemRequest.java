package com.example.bookshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private int  quantity;
    @NotNull(message = "Book ID không được để trống")
    private Long bookId;
}
