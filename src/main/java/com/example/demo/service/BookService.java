package com.example.demo.service;

import com.example.demo.dto.request.BookDTO;
import com.example.demo.model.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {
    Book createBook(BookDTO bookDTO);
    Book updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    BookDTO getBookById(Long id);
    List<Book> getAllBooks();
    void exportBooks();
}
