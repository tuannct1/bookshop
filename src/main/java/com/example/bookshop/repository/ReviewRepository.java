package com.example.bookshop.repository;

import com.example.bookshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Kiểm tra xem User đã đánh giá cuốn sách này chưa (Tránh lỗi UniqueConstraint)
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    // Lấy danh sách đánh giá của một cuốn sách (có phân trang)
    Page<Review> findByBookId(Long bookId, Pageable pageable);
}