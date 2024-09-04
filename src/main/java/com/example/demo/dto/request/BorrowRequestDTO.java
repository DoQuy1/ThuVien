package com.example.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BorrowRequestDTO {
    private Long userId;
    private List<BookBorrowDTO> booksToBorrow;
}