package com.example.bookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.bookshop.dto.request.RegisterRequest;
import com.example.bookshop.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toEntity(RegisterRequest registerRequest);
}
