package com.example.demo.service;

import com.example.demo.dto.request.BookBorrowDTO;
import com.example.demo.dto.request.BookReturnDTO;
import com.example.demo.model.BorrowForm;
import com.example.demo.dto.response.BookStatsResponse;

import java.util.List;
import java.util.Map;

public interface BorrowFormService {

    void borrowBooks(Long userId, List<BookBorrowDTO> booksToBorrow);
    void returnBooks(Long borrowFormId, List<BookReturnDTO> booksToReturn);
    List<BookStatsResponse> getMostBorrowedBooks(int limit);
    List<BookStatsResponse> getNearlyOutOfStockBooks(int threshold);
}
