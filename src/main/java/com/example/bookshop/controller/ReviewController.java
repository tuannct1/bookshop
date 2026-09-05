package com.example.bookshop.controller;

import com.example.bookshop.dto.request.ReviewRequestDTO;
import com.example.bookshop.dto.response.ReviewResponseDTO;
import com.example.bookshop.security.CustomUserDetails;
import com.example.bookshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long userId = userDetails.getId();
        ReviewResponseDTO response = reviewService.createReview(userId, requestDTO);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsByBook(
            @PathVariable Long bookId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ReviewResponseDTO> response = reviewService.getReviewsByBook(bookId, pageable);
        
        return ResponseEntity.ok(response);
    }
}