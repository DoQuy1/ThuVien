package com.example.demo.repository;

import com.example.demo.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {
    Optional<Fine> findByBorrowRecordIdAndPenaltyDate(Long borrowRecordId, LocalDate penaltyDate);
    Fine findByBorrowRecordId(Long borrowRecordId);
}
