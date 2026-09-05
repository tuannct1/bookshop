package com.example.bookshop.mapper;

import com.example.bookshop.dto.request.AuthorRequestDTO;
import com.example.bookshop.dto.response.AuthorResponseDTO;
import com.example.bookshop.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponseDTO toResponse(Author author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booksAuthored", ignore = true)
    Author toEntity(AuthorRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booksAuthored", ignore = true)
    void updateEntity(@MappingTarget Author author, AuthorRequestDTO requestDTO);
}