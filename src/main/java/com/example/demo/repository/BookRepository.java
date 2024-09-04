package com.example.demo.repository;

import com.example.demo.model.Book;
import com.example.demo.dto.response.BookStatsResponse;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT new com.example.demo.dto.response.BookStatsResponse(b.id, b.title, COUNT(bd.book.id)) " +
            "FROM BorrowDetail bd JOIN bd.book b " +
            "GROUP BY b.id " +
            "ORDER BY COUNT(bd.book.id) DESC")
    List<BookStatsResponse> findMostBorrowedBooks();

    @Query("SELECT new com.example.demo.dto.response.BookStatsResponse(b.id, b.title, CAST(b.quantity AS long)) " +
            "FROM Book b " +
            "WHERE b.quantity <= :threshold " +
            "ORDER BY b.quantity ASC")
    List<BookStatsResponse> findNearlyOutOfStockBooks(int threshold);

    Optional<Book> findByTitle(String title);
}
