package com.example.demo.repository;

import com.example.demo.dto.response.BookStatsResponse;
import com.example.demo.model.BorrowDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowDetailRepository extends JpaRepository<BorrowDetail, Long> {

    @Query("SELECT new com.example.demo.dto.response.BookStatsResponse(bd.book.id, bd.book.title, COUNT(bd.id)) " +
            "FROM BorrowDetail bd " +
            "GROUP BY bd.book.id, bd.book.title " +
            "ORDER BY COUNT(bd.id) DESC")
    List<BookStatsResponse> findTopBorrowedBooks(int limit);

    @Query("SELECT new com.example.demo.dto.response.BookStatsResponse(bd.book.title, SUM(bd.quantity) )  " +
            "FROM BorrowDetail bd " +
            "WHERE bd.borrowForm.dueDate < CURRENT_DATE " +
            "GROUP BY bd.book " +
            "HAVING SUM(bd.quantity) < :threshold " +
            "ORDER BY  SUM(bd.quantity) ASC")
    List<BookStatsResponse> findBooksLowInStock(int threshold);

    List<BorrowDetail> findByBorrowFormId(Long borrowFormId);
    List<BorrowDetail> findByBorrowRecordId(Long borrowRecordId);

}

