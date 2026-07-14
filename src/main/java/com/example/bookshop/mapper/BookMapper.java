package com.example.bookshop.mapper;

import com.example.bookshop.dto.BookRequest;
import com.example.bookshop.dto.BookResponse;
import com.example.bookshop.entity.Book;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "id", ignore = true) // Thêm mới thì ID tự tăng, bỏ qua không map
    @Mapping(target = "category", ignore = true)// Sẽ tự tìm và gán Category Object trong tầng Service sau 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Book toEntity(BookRequest bookRequest);

    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "category", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(BookRequest request, @MappingTarget Book book);
    
    // Lấy thuộc tính 'name' của đối tượng 'category' trong Entity gán vào 'categoryName' của DTO
    @Mapping(source = "category.name", target = "categoryName")
    BookResponse toResponse(Book book);

    List<BookResponse> toResponseList(List<Book> listBook);
}
