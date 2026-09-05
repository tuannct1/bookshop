package com.example.bookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.bookshop.dto.response.OrderDetailResponseDTO;
import com.example.bookshop.dto.response.OrderResponseDTO;
import com.example.bookshop.entity.Order;
import com.example.bookshop.entity.OrderDetail;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDTO toResponse(Order order);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle") 
    @Mapping(source = "book.imageUrl", target = "imageUrl")
    OrderDetailResponseDTO toDetailResponse(OrderDetail detail);
}