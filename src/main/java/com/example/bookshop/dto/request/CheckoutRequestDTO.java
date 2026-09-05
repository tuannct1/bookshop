package com.example.bookshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.example.bookshop.enums.PaymentMethod;

@Getter
@Setter
public class CheckoutRequestDTO {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    private String receiverAddress;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String receiverPhone;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    private PaymentMethod paymentMethod;

    private String note;

    @NotEmpty(message = "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán")
    private List<Long> cartItemIds;
}