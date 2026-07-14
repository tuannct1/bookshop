package com.example.bookshop.mapper;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.bookshop.dto.CategoryRequest;
import com.example.bookshop.dto.CategoryResponse;
import com.example.bookshop.entity.Category;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "books", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   @Mapping(target = "createdBy", ignore = true)
   Category toEntity(CategoryRequest categoryRequest);
   
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "books", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   @Mapping(target = "createdBy", ignore = true)
   void updateEntity(CategoryRequest request, @MappingTarget Category category);
   
   CategoryResponse toResponse(Category category);

   List<CategoryResponse> toResponseList(List<Category> Categories);
} 