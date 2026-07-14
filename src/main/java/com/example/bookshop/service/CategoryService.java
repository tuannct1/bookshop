package com.example.bookshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bookshop.dto.CategoryRequest;
import com.example.bookshop.dto.CategoryResponse;
import com.example.bookshop.entity.Category;
import com.example.bookshop.exception.CategoryNotFoundException;
import com.example.bookshop.mapper.CategoryMapper;
import com.example.bookshop.repository.CategoryRepository;

@Service
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    public CategoryResponse getCategory(Long id){
       Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
                return  categoryMapper.toResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = categoryMapper.toEntity(request) ;
    Category savedCategory = categoryRepository.save(category);

    return categoryMapper.toResponse(savedCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        categoryMapper.updateEntity(request, existingCategory);
        categoryRepository.save(existingCategory);
        return categoryMapper.toResponse(existingCategory);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
