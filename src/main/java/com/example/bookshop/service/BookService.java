package com.example.bookshop.service;

import org.springframework.data.domain.Pageable;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.bookshop.dto.request.BookRequest;
import com.example.bookshop.dto.response.BookResponse;
import com.example.bookshop.entity.Author;
import com.example.bookshop.entity.Book;
import com.example.bookshop.entity.Category;
import com.example.bookshop.entity.Publisher;
import com.example.bookshop.enums.BookStatus;
import com.example.bookshop.exception.BookNotFoundException;
import com.example.bookshop.mapper.BookMapper;
import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CategoryRepository;
import com.example.bookshop.repository.PublisherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;

    public BookResponse createBook(BookRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new BookNotFoundException("Category not found"));
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
            .orElseThrow(() -> new RuntimeException("Publisher not found"));
        List<Author> authors = authorRepository.findAllById(request.getAuthorIds());
        
        Book book = bookMapper.toEntity(request);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setAuthors(new HashSet<>(authors));
        
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    public BookResponse updateBook(Long id, BookRequest request){
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new BookNotFoundException("Category not found"));
        
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
            .orElseThrow(() -> new RuntimeException("Publisher not found"));
        List<Author> authors = authorRepository.findAllById(request.getAuthorIds());

        bookMapper.updateEntity(request, book);
        
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setAuthors(new HashSet<>(authors));
        
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toResponse(updatedBook);
    }

    public void deleteBook(Long id){
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book not found"));
        
        book.setStatus(BookStatus.DISCONTINUED);
        bookRepository.save(book);
    }

    public Page<BookResponse> getAllBook(Pageable pageable){
        Page<Book> books = bookRepository.findByStatusNot(BookStatus.DISCONTINUED, pageable);     
        return books.map(bookMapper::toResponse);
    }

    public Page<BookResponse> getBooksByCategory(Long categoryId, Pageable pageable) {
        Page<Book> books = bookRepository.findByCategoryIdAndStatusNot(categoryId, BookStatus.DISCONTINUED, pageable);
        return books.map(bookMapper::toResponse);
    }

    public BookResponse getBook(Long id){
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book not found"));
        return bookMapper.toResponse(book);
    }

    public Page<BookResponse> searchBooksVIP(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Book> books = bookRepository.searchBooksVIP(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
                categoryId,
                minPrice,
                maxPrice,
                pageable
        );
        return books.map(bookMapper::toResponse);
    }
    public Page<Book> getFilteredBooks(Long categoryId, String keyword, Pageable pageable) {
    return bookRepository.filterBooks(categoryId, keyword, pageable);
}
}