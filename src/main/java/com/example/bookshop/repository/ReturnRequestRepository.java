package com.example.bookshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookshop.entity.ReturnRequest;
import com.example.bookshop.enums.ReturnStatus;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long>{
Page<ReturnRequest> findByStatus(ReturnStatus status, Pageable pageable);
Page<ReturnRequest> findByOrderUserId(Long userId, Pageable pageable);
} 