package com.example.bookshop.service;

import com.example.bookshop.dto.request.CheckoutRequestDTO;
import com.example.bookshop.dto.response.OrderResponseDTO;
import com.example.bookshop.entity.*;
import com.example.bookshop.enums.BookStatus;
import com.example.bookshop.enums.OrderStatus;
import com.example.bookshop.enums.PaymentMethod;
import com.example.bookshop.enums.PaymentStatus;
import com.example.bookshop.mapper.OrderMapper;
import com.example.bookshop.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository; 
    private final BookRepository bookRepository; 
    private final OrderMapper orderMapper;
    private final VNPayService vnPayService;

    @Transactional
    public OrderResponseDTO checkout(Long userId, CheckoutRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống hoặc các sản phẩm không tồn tại.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setPaymentMethod(request.getPaymentMethod()); 
        order.setNote(request.getNote());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID); 

        double totalPrice = 0;
        Set<OrderDetail> orderDetails = new HashSet<>();
        List<Book> booksToUpdate = new ArrayList<>(); 

        for (CartItem item : cartItems) {
            Book book = item.getBook();

            if (book.getStatus() != BookStatus.AVAILABLE) { 
                throw new RuntimeException("Sách '" + book.getTitle() + "' hiện không mở bán."); 
            }

            if (book.getQuantity() < item.getQuantity()) { 
                throw new RuntimeException("Sách '" + book.getTitle() + "' không đủ số lượng. Kho chỉ còn: " + book.getQuantity()); 
            }

            book.setQuantity(book.getQuantity() - item.getQuantity()); 
            booksToUpdate.add(book);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setBook(book);
            detail.setQuantity(item.getQuantity());
            
            double currentPrice = book.getPrice() != null ? book.getPrice() : 0.0; 
            detail.setPrice(currentPrice);
            detail.setSubtotal(currentPrice * item.getQuantity());

            orderDetails.add(detail);
            totalPrice += detail.getSubtotal();
        }

        order.setTotalPrice(totalPrice);
        order.setOrderDetails(orderDetails); 
        Order savedOrder = orderRepository.save(order); 
        bookRepository.saveAll(booksToUpdate);         
        cartItemRepository.deleteAll(cartItems);        
        OrderResponseDTO responseDTO = orderMapper.toResponse(savedOrder);
        if (request.getPaymentMethod() == PaymentMethod.VNPAY) {
        String vnpayUrl = vnPayService.createPaymentUrl(savedOrder.getId(), savedOrder.getTotalPrice(), "127.0.0.1");
        responseDTO.setPaymentUrl(vnpayUrl);
    } else {
        responseDTO.setPaymentUrl(null); 
    }
    return responseDTO;    
}
    public List<OrderResponseDTO> getMyOrders(Long userId, OrderStatus status) {
            List<Order> orders;
            
            if (status != null) {
                orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
            } else {
                orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
            }      
            return orders.stream()
                    .map(orderMapper::toResponse)
                    .collect(Collectors.toList());
        }

    public OrderResponseDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền truy cập đơn hàng này.");
        }

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponseDTO cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Không thể hủy đơn hàng đang ở trạng thái: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELED);

        List<Book> booksToUpdate = new ArrayList<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            Book book = detail.getBook();
            book.setQuantity(book.getQuantity() + detail.getQuantity());
            booksToUpdate.add(book);
        }

        bookRepository.saveAll(booksToUpdate);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }
    @Transactional
    public void reOrder(Long userId, Long orderId) {
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!oldOrder.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên đơn hàng này.");
        }

        if (oldOrder.getStatus() != OrderStatus.DELIVERED && oldOrder.getStatus() != OrderStatus.CANCELED) {
            throw new RuntimeException("Chỉ có thể mua lại đơn hàng đã hoàn thành hoặc đã hủy.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        for (OrderDetail detail : oldOrder.getOrderDetails()) {
            Book book = detail.getBook();

            if (book.getStatus() != BookStatus.AVAILABLE || book.getQuantity() <= 0) {
                continue; 
            }

            int quantityToAdd = Math.min(detail.getQuantity(), book.getQuantity());

            Optional<CartItem> existingCartItem = cartItemRepository.findByUserIdAndBookId(userId, book.getId());

            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                int newQuantity = cartItem.getQuantity() + quantityToAdd;
                
                cartItem.setQuantity(Math.min(newQuantity, book.getQuantity()));
                cartItemRepository.save(cartItem);
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setUser(user);
                newCartItem.setBook(book);
                newCartItem.setQuantity(quantityToAdd);
                cartItemRepository.save(newCartItem);
            }
        }
    }
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getAllOrdersForAdmin(OrderStatus status, Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByFiltersForAdmin(status, userId, pageable);
        return orders.map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByIdForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.RETURNED) {
            throw new RuntimeException("Không thể thay đổi trạng thái của đơn hàng đã hủy hoặc đã hoàn trả.");
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        
        return orderMapper.toResponse(savedOrder);
    }
    @Transactional
    public OrderResponseDTO confirmReceipt(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên đơn hàng này.");
        }

        if (order.getStatus() != OrderStatus.SHIPPING) {
            throw new RuntimeException("Chỉ có thể xác nhận đã nhận với đơn hàng đang được giao (SHIPPING).");
        }

        order.setStatus(OrderStatus.DELIVERED);

        if (order.getPaymentMethod() == PaymentMethod.COD && order.getPaymentStatus() == PaymentStatus.UNPAID) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }
    @Transactional
    public void updatePaymentStatus(Long orderId, PaymentStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setPaymentStatus(newStatus);
        orderRepository.save(order);
    }
}