package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class NotiEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "borrow_record_id")
    private Long borrowRecordId;

    @Column(name = "email")
    private String email;

    @Column(name = "book_title")
    private String bookTitle;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "penalty_amount")
    private Double penaltyAmount;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "status")
    private String status; //"DONE", "FAIL"

}
