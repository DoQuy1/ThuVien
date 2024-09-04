package com.example.demo.dto.request;

import lombok.Data;

@Data
public class BookBorrowStatusDTO {
    private Long bookId;
    private Integer borrowedQuantity;
    private Integer returnedQuantity;
}
