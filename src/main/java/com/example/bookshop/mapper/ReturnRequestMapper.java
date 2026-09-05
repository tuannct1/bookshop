package com.example.bookshop.mapper;

import com.example.bookshop.dto.response.ReturnResponseDTO;
import com.example.bookshop.entity.ReturnRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReturnRequestMapper {

    @Mapping(source = "order.id", target = "orderId")
    ReturnResponseDTO toResponse(ReturnRequest returnRequest);
}