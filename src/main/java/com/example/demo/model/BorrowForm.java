package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "borrow_form")
@Data
public class BorrowForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "borrow_date", nullable = false)
    private Date borrowDate;

    @Column(name = "return_date")
    private Date returnDate;

    @Column(name = "due_date", nullable = false)
    private Date dueDate;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "borrowForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowDetail> borrowDetails = new ArrayList<>();


}
