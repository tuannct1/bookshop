package com.example.bookshop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.bookshop.entity.Category;
import com.example.bookshop.repository.CategoryRepository;

@Service
public class CategoryService {
    
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category c) {
        return categoryRepository.save(c);
    }

    public Category updateCategory(Long id, Category c) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        existingCategory.setName(c.getName());
        existingCategory.setDescription(c.getDescription());
        
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
