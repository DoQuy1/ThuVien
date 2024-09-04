package com.example.demo.service.impl;

import com.example.demo.dto.request.BookDTO;
import com.example.demo.dto.request.UserDTO;
import com.example.demo.model.Book;
import com.example.demo.model.Author;
import com.example.demo.model.Category;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Book createBook(BookDTO bookDTO) {

        // Kiểm tra xem sách đã tồn tại chưa
        Optional<Book> existingBook = bookRepository.findByTitle(bookDTO.getTitle());
        if (existingBook.isPresent()) {
            throw new RuntimeException("Sách đã tồn tại: " + bookDTO.getTitle());
        }

        Set<Author> authors = new HashSet<>();
        Set<Category> categories = new HashSet<>();

        for (String authorName : bookDTO.getAuthorNames()) {
            Optional<Author> existingAuthor = authorRepository.findByName(authorName);
            existingAuthor.ifPresentOrElse(authors::add, () -> {
                Author newAuthor = new Author();
                newAuthor.setName(authorName);
                authors.add(authorRepository.save(newAuthor));
            });
        }

        for (String categoryName : bookDTO.getCategoryNames()) {
            Optional<Category> existingCategory = categoryRepository.findByName(categoryName);
            existingCategory.ifPresentOrElse(categories::add, () -> {
                Category newCategory = new Category();
                newCategory.setName(categoryName);
                categories.add(categoryRepository.save(newCategory));
            });
        }

        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setDescription(bookDTO.getDescription());
        book.setQuantity(bookDTO.getQuantity());
        book.setAuthors(authors);
        book.setCategories(categories);

        return bookRepository.save(book);
    }


    @Override
    public Book updateBook(Long id, BookDTO bookDTO) {
        // Kiểm tra xem sách có tồn tại không
        Optional<Book> existingBookOptional = bookRepository.findById(id);
        if (!existingBookOptional.isPresent()) {
            throw new RuntimeException("Sách không tồn tại với ID: " + id);
        }

        Book existingBook = existingBookOptional.get();

        // Cập nhật thông tin sách
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setQuantity(bookDTO.getQuantity());

        // tác giả
        Set<Author> authors = new HashSet<>();
        for (String authorName : bookDTO.getAuthorNames()) {
            Optional<Author> existingAuthor = authorRepository.findByName(authorName);
            existingAuthor.ifPresentOrElse(authors::add, () -> {
                Author newAuthor = new Author();
                newAuthor.setName(authorName);
                authors.add(authorRepository.save(newAuthor));
            });
        }
        existingBook.setAuthors(authors);

        // thể loại
        Set<Category> categories = new HashSet<>();
        for (String categoryName : bookDTO.getCategoryNames()) {
            Optional<Category> existingCategory = categoryRepository.findByName(categoryName);
            existingCategory.ifPresentOrElse(categories::add, () -> {
                Category newCategory = new Category();
                newCategory.setName(categoryName);
                categories.add(categoryRepository.save(newCategory));
            });
        }
        existingBook.setCategories(categories);

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long bookId) {
        if (bookRepository.existsById(bookId)) {
            bookRepository.deleteById(bookId);
        } else {
            throw new RuntimeException("Sách không tồn tại: " + bookId);
        }
    }

    @Override
    public BookDTO getBookById(Long id) {
        try {
            Book extBook = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            return convertToDTO(extBook);
        } catch (Exception e) {
            e.printStackTrace(); // In ra stack trace để debug
            throw new RuntimeException("An error occurred while retrieving the book", e);
        }
    }

    private BookDTO convertToDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    @Override
    public void exportBooks() {
        List<Book> books = bookRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Books");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Title");
            headerRow.createCell(1).setCellValue("Description");
            headerRow.createCell(2).setCellValue("Quantity");

            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getTitle());
                row.createCell(1).setCellValue(book.getDescription());
                row.createCell(2).setCellValue(book.getQuantity());
            }

            try (FileOutputStream fileOut = new FileOutputStream("books.xlsx")) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export books", e);
        }
    }
}
