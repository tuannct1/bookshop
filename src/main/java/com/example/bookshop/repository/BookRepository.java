package com.example.bookshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bookshop.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}  
    

