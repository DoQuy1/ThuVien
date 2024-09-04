package com.example.demo.dto.request;

import lombok.Data;

@Data
public class BookBorrowDTO {
    private Long bookId;
    private Integer quantity;
}
