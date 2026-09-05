package com.example.bookshop.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.bookshop.dto.request.CartItemRequest;
import com.example.bookshop.dto.response.CartItemResponse;
import com.example.bookshop.entity.Book;
import com.example.bookshop.entity.CartItem;
import com.example.bookshop.entity.User;
import com.example.bookshop.exception.BookNotFoundException;
import com.example.bookshop.exception.CartItemNotFoundExeption;
import com.example.bookshop.exception.UserNotFoundException;
import com.example.bookshop.mapper.CartItemMapper;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CartItemRepository;
import com.example.bookshop.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartItemResponse createCartItem(Long userId, CartItemRequest cartItemRequest){
        Book book = bookRepository.findById(cartItemRequest.getBookId())
        .orElseThrow(() -> new BookNotFoundException("Book not found"));
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndBookId(user.getId(), book.getId());
        CartItem cartItem; 
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequest.getQuantity());
        } else {
            cartItem = cartItemMapper.toEntity(cartItemRequest);
            cartItem.setBook(book);
            cartItem.setUser(user);
        }
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toResponse(cartItem);
    }

    public Page<CartItemResponse> getAllCartItem(Long userId, Pageable pageable){
        Page<CartItem> cartItems= cartItemRepository.findByUserId(userId, pageable);     
        return cartItems.map(cartItemMapper::toResponse);
    } 
    public CartItemResponse updateCartItem(Long userId, Long id, CartItemRequest cartItemRequest){
        CartItem cartItem = cartItemRepository.findById(id)
        .orElseThrow(() -> new CartItemNotFoundExeption("cartItem not found"));
        
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa sản phẩm này trong giỏ hàng");
        }
        cartItem.setQuantity(cartItemRequest.getQuantity());       
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toResponse(cartItem);
    }
    public void deleteCartItem(Long userId, Long id){
        CartItem cartItem = cartItemRepository.findById(id)
        .orElseThrow(() -> new CartItemNotFoundExeption("cartItem not found"));      
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa sản phẩm này");
        }
        cartItemRepository.delete(cartItem);
    }
    public CartItemResponse getCartItem(Long userId, Long id){
        CartItem cartItem = cartItemRepository.findById(id)
        .orElseThrow(() -> new CartItemNotFoundExeption("cartItem not found"));     
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem sản phẩm này");
        }
        return cartItemMapper.toResponse(cartItem);
    }
}
