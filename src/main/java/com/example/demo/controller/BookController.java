package com.example.demo.controller;

import com.example.demo.dto.request.BookDTO;
import com.example.demo.dto.response.Response;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.example.demo.common.ReponseMessage.*;
import static com.example.demo.common.ResponseCode.ERROR_CODE;
import static com.example.demo.common.ResponseCode.SUCCESS_CODE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Response<Book>> createBook(@RequestBody BookDTO bookDTO) {
        try {
            Book book = bookService.createBook(bookDTO);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, CREATE_BOOK_SUCCESSFULLY, book)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, ERROR_CREATING_BOOK)
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Book>> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        try {
            Book book = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, UPDATE_BOOK_SUCCESSFULLY, book)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, ERROR_UPDATING_BOOK)
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, DELETE_BOOK_SUCCESSFULLY, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, ERROR_DELETING_BOOK)
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<BookDTO>> getBookById(@PathVariable Long id) {
        try {
            BookDTO book = bookService.getBookById(id);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, "Book fetched successfully", book)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, BOOK_NOT_FOUND)
            );
        }
    }

    @GetMapping("/export")
    public ResponseEntity<Response<String>> exportBooks() {
        try {
            bookService.exportBooks();
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, EXPORT_BOOK_SUCCESSFULLY, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, "Export books failed")
            );
        }
    }
}
