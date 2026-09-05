package com.example.bookshop.service;

import com.example.bookshop.dto.request.ReturnRequestDTO;
import com.example.bookshop.dto.response.ReturnResponseDTO;
import com.example.bookshop.entity.Book;
import com.example.bookshop.entity.Order;
import com.example.bookshop.entity.OrderDetail;
import com.example.bookshop.entity.ReturnRequest;
import com.example.bookshop.enums.OrderStatus;
import com.example.bookshop.enums.ReturnStatus;
import com.example.bookshop.mapper.ReturnRequestMapper;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.OrderRepository;
import com.example.bookshop.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {
    private final BookRepository bookRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final ReturnRequestMapper returnRequestMapper;

    @Transactional
    public ReturnResponseDTO createRequest(Long userId, Long orderId, ReturnRequestDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên đơn hàng này.");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Chỉ có thể yêu cầu trả hàng với các đơn đã giao thành công.");
        }

        ReturnRequest request = new ReturnRequest();
        request.setOrder(order);
        request.setReason(dto.getReason()); 
        request.setProofImages(dto.getProofImages()); 
        request.setRefundAmount(order.getTotalPrice());
        request.setStatus(ReturnStatus.PENDING);

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        orderRepository.save(order);

        ReturnRequest savedRequest = returnRequestRepository.save(request);

        return returnRequestMapper.toResponse(savedRequest);
    }
    @Transactional
    public ReturnResponseDTO updateReturnStatus(Long requestId, ReturnStatus newStatus) {
        ReturnRequest request = returnRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hoàn trả với ID: " + requestId));

        request.setStatus(newStatus); 

        if (newStatus == ReturnStatus.COMPLETED) {
            Order order = request.getOrder(); //[cite: 4]
            
            order.setStatus(OrderStatus.RETURNED); 
            
            List<Book> booksToUpdate = new ArrayList<>();
            for (OrderDetail detail : order.getOrderDetails()) {
                Book book = detail.getBook();
                book.setQuantity(book.getQuantity() + detail.getQuantity());
                booksToUpdate.add(book);
            }
            bookRepository.saveAll(booksToUpdate);
            orderRepository.save(order);
            
        } else if (newStatus == ReturnStatus.REJECTED) {
            Order order = request.getOrder(); //[cite: 4]
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        }

        ReturnRequest savedRequest = returnRequestRepository.save(request);

        return returnRequestMapper.toResponse(savedRequest); //[cite: 1]
    }
    @Transactional(readOnly = true)
    public Page<ReturnResponseDTO> getAllRequestsForAdmin(ReturnStatus status, Pageable pageable) {
        Page<ReturnRequest> requests;
        
        if (status != null) {
            requests = returnRequestRepository.findByStatus(status, pageable);
        } else {
            requests = returnRequestRepository.findAll(pageable);
        }
        
        return requests.map(returnRequestMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReturnResponseDTO> getMyReturnRequests(Long userId, Pageable pageable) {
        Page<ReturnRequest> requests = returnRequestRepository.findByOrderUserId(userId, pageable);
        return requests.map(returnRequestMapper::toResponse);
    }
}
