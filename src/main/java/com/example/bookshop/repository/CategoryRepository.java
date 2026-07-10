package com.example.bookshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bookshop.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    
} 