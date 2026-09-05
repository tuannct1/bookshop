package com.example.bookshop.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bookshop.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
Page<CartItem> findByUserId(Long userId, Pageable pageable);
Optional<CartItem> findByUserIdAndBookId(Long userId, Long bookId);
    
} 