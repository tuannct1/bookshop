package com.example.bookshop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.bookshop.entity.Category;
import com.example.bookshop.service.CategoryService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/categories")
public class CategoryController {

   private final CategoryService categoryService;
   public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }
    
    @PostMapping
    public Category createCategory(@Valid @RequestBody Category c) {
        return categoryService.createCategory(c);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id,@Valid @RequestBody Category c) {     
        return categoryService.updateCategory(id, c);
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "Category deleted successfully";
    }
    
}
