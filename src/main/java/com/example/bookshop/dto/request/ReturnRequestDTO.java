package com.example.bookshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReturnRequestDTO {

    @NotBlank(message = "Lý do trả hàng không được để trống")
    @Size(max = 1000, message = "Lý do trả hàng không được vượt quá 1000 ký tự")
    private String reason;

    private String proofImages;
}