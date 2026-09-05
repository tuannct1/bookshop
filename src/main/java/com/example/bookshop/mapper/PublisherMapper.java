package com.example.bookshop.mapper;

import com.example.bookshop.dto.request.PublisherRequestDTO;
import com.example.bookshop.dto.response.PublisherResponseDTO;
import com.example.bookshop.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherResponseDTO toResponse(Publisher publisher);

    @Mapping(target = "id", ignore = true)
    Publisher toEntity(PublisherRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Publisher publisher, PublisherRequestDTO requestDTO);
}