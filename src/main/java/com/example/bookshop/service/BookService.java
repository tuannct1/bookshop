package com.example.bookshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bookshop.dto.BookRequest;
import com.example.bookshop.dto.BookResponse;
import com.example.bookshop.entity.Book;
import com.example.bookshop.entity.Category;
import com.example.bookshop.exception.BookNotFoundException;
import com.example.bookshop.mapper.BookMapper;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CategoryRepository;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
    }

    public BookResponse createBook(BookRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new BookNotFoundException("Category not found"));
        Book book = bookMapper.toEntity(request);
        book.setCategory(category);
        Book savedBook= bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    public BookResponse updateBook(Long id, BookRequest request){
        Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException("Book not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new BookNotFoundException("Category not found"));
        bookMapper.updateEntity(request, book);
        book.setCategory(category);
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toResponse(updatedBook);
    }

    public void deleteBook(Long id){
        Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException("Book not found"));

        bookRepository.delete(book);
    }

    public List<BookResponse> getAllBook(){
        List<Book> books = bookRepository.findAll();
        return bookMapper.toResponseList(books);
    }

    public BookResponse getBook(Long id){
        Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException("Book not found"));
        return bookMapper.toResponse(book);
    }
} 
