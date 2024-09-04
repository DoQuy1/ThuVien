package com.example.demo.repository;

import com.example.demo.model.BorrowForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface BorrowFormRepository extends JpaRepository<BorrowForm, Long> {


        @Query("SELECT bf FROM BorrowForm bf " +
                "LEFT JOIN NotiEmail ne ON ne.borrowRecordId = bf.id " +
                "WHERE ne.borrowRecordId IS NULL " +  // Lọc ra các bản ghi chưa có trong noti_email
                "AND bf.status = 'BORROWED' " +
                "AND (bf.dueDate BETWEEN :warningDate AND :now OR bf.dueDate < :now)")
        List<BorrowForm> findUpcomingAndOverdueBorrowForms(
                @Param("warningDate") Date warningDate,
                @Param("now") Date now);


    @Query("SELECT SUM(bd.quantity) FROM BorrowDetail bd JOIN bd.borrowForm bf WHERE bf.user.id = ?1 AND bf.status = 'BORROWED'")
    Integer countTotalBorrowedBooksByUserId(Long userId);
    List<BorrowForm> findByStatusIn(List<String> statuses);

    List<BorrowForm> findByUserId(Long userId);


}

