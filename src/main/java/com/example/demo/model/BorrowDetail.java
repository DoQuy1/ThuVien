package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "borrow_detail")
@Data
public class BorrowDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "borrow_record_id")
    private Long borrowRecordId;

    @Column(name = "returned_quantity", nullable = false)
    private int returnedQuantity = 0; // Khởi tạo mặc định là 0

    @ManyToOne
    @JoinColumn(name = "book_id", insertable = false, updatable = false) // Đảm bảo không chèn hoặc cập nhật book_id
    private Book book;

    @ManyToOne
    @JoinColumn(name = "borrow_form_id", nullable = false)
    private BorrowForm borrowForm;


}
