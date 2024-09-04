package com.example.demo.controller;

import com.example.demo.dto.request.BookBorrowDTO;
import com.example.demo.dto.request.BorrowRequestDTO;
import com.example.demo.dto.request.ReturnRequestDTO;
import com.example.demo.dto.response.BookStatsResponse;
import com.example.demo.model.BorrowForm;
import com.example.demo.service.BorrowFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.example.demo.dto.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.demo.common.ResponseCode.ERROR_CODE;
import static com.example.demo.common.ResponseCode.SUCCESS_CODE;

@RestController
@RequestMapping("/api/v1/borrow-forms")
public class BorrowFormController {

    @Autowired
    private BorrowFormService borrowFormService;

    @ApiOperation(value = "Borrow books")
    @PostMapping("/borrow")
    public ResponseEntity<Response<Void>> borrowBooks(@RequestBody BorrowRequestDTO borrowRequest) {
        try {
            borrowFormService.borrowBooks(borrowRequest.getUserId(), borrowRequest.getBooksToBorrow());
            return ResponseEntity.ok().body(new Response<>(SUCCESS_CODE, "Books borrowed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(ERROR_CODE, e.getMessage(), null));
        }
    }


    @ApiOperation(value = "Return books")
    @PostMapping("/return")
    public ResponseEntity<Response<String>> returnBooks(@RequestBody ReturnRequestDTO returnRequest) {
        try {
            borrowFormService.returnBooks(returnRequest.getBorrowFormId(), returnRequest.getBooksToReturn());
            return ResponseEntity.ok().body(new Response<>(SUCCESS_CODE, "Books returned successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(ERROR_CODE, e.getMessage(), null));
        }
    }


    @ApiOperation(value = "Get most borrowed books")
    @GetMapping("/most-borrowed-books")
    public ResponseEntity<Response<List<BookStatsResponse>>> getMostBorrowedBooks(@RequestParam int limit) {
        try {
            List<BookStatsResponse> books = borrowFormService.getMostBorrowedBooks(limit);
            return ResponseEntity.ok().body(new Response<>(SUCCESS_CODE, "Fetched most borrowed books", books));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(ERROR_CODE, e.getMessage(), null));
        }
    }

    @ApiOperation(value = "Get nearly out of stock books")
    @GetMapping("/nearly-out-of-stock-books")
    public ResponseEntity<Response<List<BookStatsResponse>>> getNearlyOutOfStockBooks(@RequestParam int threshold) {
        try {
            List<BookStatsResponse> books = borrowFormService.getNearlyOutOfStockBooks(threshold);
            return ResponseEntity.ok().body(new Response<>(SUCCESS_CODE, "Fetched nearly out of stock books", books));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(ERROR_CODE, e.getMessage(), null));
        }
    }
}
