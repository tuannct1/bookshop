package com.example.bookshop.repository;

import com.example.bookshop.entity.OrderDetail;
import com.example.bookshop.enums.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {


    List<OrderDetail> findByOrderId(Long orderId);

    List<OrderDetail> findByBookId(Long bookId);
    boolean existsByOrderUserIdAndBookIdAndOrderStatus(Long userId, Long bookId, OrderStatus status);
}