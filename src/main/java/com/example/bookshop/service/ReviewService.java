package com.example.bookshop.service;

import com.example.bookshop.dto.request.ReviewRequestDTO;
import com.example.bookshop.dto.response.ReviewResponseDTO;
import com.example.bookshop.entity.Book;
import com.example.bookshop.entity.Review;
import com.example.bookshop.entity.User;
import com.example.bookshop.enums.OrderStatus;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.OrderDetailRepository;
import com.example.bookshop.repository.ReviewRepository;
import com.example.bookshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ReviewResponseDTO createReview(Long userId, ReviewRequestDTO request) {
        boolean hasPurchased = orderDetailRepository.existsByOrderUserIdAndBookIdAndOrderStatus(
                userId, request.getBookId(), OrderStatus.DELIVERED);
        
        if (!hasPurchased) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và nhận hàng thành công.");
        }

        if (reviewRepository.existsByUserIdAndBookId(userId, request.getBookId())) {
            throw new RuntimeException("Bạn đã đánh giá cuốn sách này rồi.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
        
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuốn sách này"));

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .book(book)
                .build();

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getReviewsByBook(Long bookId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);
        return reviews.map(this::mapToResponse);
    }

    private ReviewResponseDTO mapToResponse(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .userName(review.getUser().getEmail()) // Giả sử Entity User có getFullName()
                .createdAt(review.getCreatedAt())
                .build();
    }
}