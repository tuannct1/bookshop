package com.example.bookshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bookshop.entity.Book;
import com.example.bookshop.enums.BookStatus;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByCategoryIdAndStatusNot(Long categoryId, BookStatus status, Pageable pageable);

    Page<Book> findByStatusNot(BookStatus status, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.authors a " +
           "WHERE b.status != com.example.bookshop.enums.BookStatus.DISCONTINUED " +
           "AND (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR b.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR b.price <= :maxPrice)")
    Page<Book> searchBooksVIP(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
    boolean existsByTitle(String title);

    @Query("SELECT b FROM Book b WHERE " +
           "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> filterBooks(@Param("categoryId") Long categoryId, 
                           @Param("keyword") String keyword, 
                           Pageable pageable);
}