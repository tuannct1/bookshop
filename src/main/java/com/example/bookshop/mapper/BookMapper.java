package com.example.bookshop.mapper;

import com.example.bookshop.dto.request.BookRequest;
import com.example.bookshop.dto.response.BookResponse;
import com.example.bookshop.entity.Author;
import com.example.bookshop.entity.Book;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Book toEntity(BookRequest bookRequest);

    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "category", ignore = true) 
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(BookRequest request, @MappingTarget Book book);
    
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "authors", target = "authorNames", qualifiedByName = "mapAuthorNames")
    @Mapping(source = "publisher.name", target = "publisherName")
    BookResponse toResponse(Book book);
    @Named("mapAuthorNames")
    
    default List<String> mapAuthorNames(Set<Author> authors) {
        if (authors == null) {
            return null;
        }
        return authors.stream()
                      .map(Author::getName)
                      .collect(Collectors.toList());
    }

    List<BookResponse> toResponseList(List<Book> listBook);
}
