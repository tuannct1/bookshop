package com.example.bookshop.exception;

public class CartItemNotFoundExeption extends RuntimeException{
    public CartItemNotFoundExeption(String message) {
        super(message);
    }
}
