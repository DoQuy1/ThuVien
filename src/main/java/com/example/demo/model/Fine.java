package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "fine")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "borrow_record_id", nullable = false)
    private Long borrowRecordId;

    @Column(name = "penalty_amount", nullable = false)
    private BigDecimal penaltyAmount;

    @Column(name = "penalty_date", nullable = false)
    private LocalDate penaltyDate;
}
