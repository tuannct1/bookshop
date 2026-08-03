package com.example.bookshop.repository;

import com.example.bookshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Hàm này rất quan trọng để Spring Security tìm kiếm user khi đăng nhập
    Optional<User> findByUsername(String username);
}