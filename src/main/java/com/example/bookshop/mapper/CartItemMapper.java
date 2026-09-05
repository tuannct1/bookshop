package com.example.bookshop.mapper;

import com.example.bookshop.dto.request.CartItemRequest;
import com.example.bookshop.dto.response.CartItemResponse;
import com.example.bookshop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "user", ignore = true)
    CartItem toEntity(CartItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequest(CartItemRequest request, @MappingTarget CartItem cartItem);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookName") 
    @Mapping(source = "book.price", target = "price") 
    @Mapping(source = "book.imageUrl", target = "imageUrl") 
    CartItemResponse toResponse(CartItem cartItem);   

    List<CartItemResponse> toResponseList(List<CartItem> cartItems);
}