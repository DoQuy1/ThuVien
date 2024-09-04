package com.example.demo.service.impl;

import com.example.demo.dto.request.BookBorrowDTO;
import com.example.demo.dto.request.BookBorrowStatusDTO;
import com.example.demo.dto.request.BookReturnDTO;
import com.example.demo.model.*;
import com.example.demo.dto.response.BookStatsResponse;
import com.example.demo.repository.*;
import com.example.demo.service.BorrowFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowFormServiceImpl implements BorrowFormService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BorrowFormRepository borrowFormRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowDetailRepository borrowDetailRepository;

    @Transactional
    @Override
    public void borrowBooks(Long userId, List<BookBorrowDTO> booksToBorrow) {
        // Tìm người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tính tổng số sách mà người dùng đã mượn
        int totalBorrowedBooks = borrowFormRepository.countTotalBorrowedBooksByUserId(userId);

        // Tính tổng số sách muốn mượn
        int requestedBooks = booksToBorrow.stream().mapToInt(BookBorrowDTO::getQuantity).sum();

        // Kiểm tra nếu tổng số sách muốn mượn vượt quá giới hạn
        if (totalBorrowedBooks + requestedBooks > 5) {
            throw new RuntimeException("Không thể mượn quá 5 quyển");
        }

        // Kiểm tra và cập nhật số lượng sách
        for (BookBorrowDTO dto : booksToBorrow) {
            Long bookId = dto.getBookId();
            int quantity = dto.getQuantity();

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));

            if (book.getQuantity() < quantity) {
                throw new RuntimeException("Số lượng sách còn lại không đủ để mượn");
            }

            book.setQuantity(book.getQuantity() - quantity);
            bookRepository.save(book);
        }

        // Tạo BorrowForm và BorrowDetail
        BorrowForm borrowForm = new BorrowForm();
        borrowForm.setUser(user);
        borrowForm.setBorrowDate(new Date());
        borrowForm.setDueDate(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        borrowForm.setStatus("BORROWED");
        BorrowForm savedBorrowForm = borrowFormRepository.save(borrowForm);

        for (BookBorrowDTO dto : booksToBorrow) {
            BorrowDetail borrowDetail = new BorrowDetail();
            borrowDetail.setBorrowForm(savedBorrowForm);  // Thiết lập BorrowForm
            borrowDetail.setBookId(dto.getBookId());
            borrowDetail.setQuantity(dto.getQuantity());
            borrowDetailRepository.save(borrowDetail);
        }
    }

    @Transactional
    @Override
    public void returnBooks(Long borrowFormId, List<BookReturnDTO> booksToReturn) {
        // Tìm BorrowForm theo borrowFormId
        BorrowForm borrowForm = borrowFormRepository.findById(borrowFormId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        // Lấy danh sách BorrowDetail theo borrowFormId
        List<BorrowDetail> details = borrowForm.getBorrowDetails();

        // Tạo danh sách BookBorrowStatusDTO để lưu số lượng mượn còn lại
        List<BookBorrowStatusDTO> bookStatuses = details.stream()
                .map(detail -> {
                    BookBorrowStatusDTO status = new BookBorrowStatusDTO();
                    status.setBookId(detail.getBook().getId());
                    status.setBorrowedQuantity(detail.getQuantity());
                    status.setReturnedQuantity(detail.getReturnedQuantity());
                    return status;
                })
                .collect(Collectors.toList());

        // Kiểm tra và cập nhật thông tin trả về
        boolean allReturned = true;

        for (BookReturnDTO returnDTO : booksToReturn) {
            Long bookId = returnDTO.getBookId();
            int returnQuantity = returnDTO.getQuantity();

            // Tìm BookBorrowStatusDTO tương ứng với bookId
            BookBorrowStatusDTO statusDTO = bookStatuses.stream()
                    .filter(status -> status.getBookId().equals(bookId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy id sách: " + bookId));

            int borrowedQuantity = statusDTO.getBorrowedQuantity();
            int returnedQuantity = statusDTO.getReturnedQuantity() + returnQuantity;

            // Kiểm tra số lượng trả về không vượt quá số lượng mượn còn lại
            if (returnedQuantity > borrowedQuantity) {
                throw new RuntimeException("Số lượng trả vượt quá số lượng mượn: " + bookId);
            }

            // Cập nhật số lượng sách
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
            book.setQuantity(book.getQuantity() + returnQuantity);
            bookRepository.save(book);

            // Cập nhật thông tin trong BorrowDetail
            BorrowDetail detail = details.stream()
                    .filter(d -> d.getBook().getId().equals(bookId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin trong BorrowDetail: " + bookId));

            detail.setReturnedQuantity(returnedQuantity);
            borrowDetailRepository.save(detail);

            // Cập nhật trạng thái sách trả về
            if (returnedQuantity < borrowedQuantity) {
                allReturned = false;
            }
        }

        // Cập nhật thời gian trả sách trong BorrowForm
        borrowForm.setReturnDate(new Date());

        // Kiểm tra xem tất cả sách đã được trả về chưa
        allReturned = allReturned && bookStatuses.stream()
                .allMatch(status -> status.getReturnedQuantity().equals(status.getBorrowedQuantity()));

        // Cập nhật trạng thái của BorrowForm
        borrowForm.setStatus(allReturned ? "RETURNED" : "PRE_RETURNED");
        borrowFormRepository.save(borrowForm);
    }

    @Override
    public List<BookStatsResponse> getMostBorrowedBooks(int limit) {
        return borrowDetailRepository.findTopBorrowedBooks(limit);
    }

    @Override
    public List<BookStatsResponse> getNearlyOutOfStockBooks(int threshold) {
        return bookRepository.findNearlyOutOfStockBooks(threshold);
    }
}
