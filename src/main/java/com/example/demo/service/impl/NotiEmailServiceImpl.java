package com.example.demo.service.impl;

import com.example.demo.model.BorrowForm;
import com.example.demo.model.BorrowDetail;
import com.example.demo.model.NotiEmail;
import com.example.demo.model.Book;
import com.example.demo.model.User;
import com.example.demo.model.Fine;
import com.example.demo.repository.*;
import com.example.demo.service.NotiEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class NotiEmailServiceImpl implements NotiEmailService {

    @Autowired
    private BorrowFormRepository borrowFormRepository;

    @Autowired
    private BorrowDetailRepository borrowDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotiEmailRepository notiEmailRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private BookRepository bookRepository;

    @Scheduled(cron = "0 0 8 * * ?")
    @Override
    public void updateNotiEmailTable() {
        LocalDate now = LocalDate.now();
        LocalDate warningDate = now.plusDays(3);

        Date warningDateForQuery = convertToDate(warningDate);
        Date nowForQuery = convertToDate(now);

        // Lấy danh sách phiếu mượn sắp đến hạn và quá hạn
        List<BorrowForm> allForms = borrowFormRepository.findUpcomingAndOverdueBorrowForms(warningDateForQuery, nowForQuery);

        // Lưu thông tin vào bảng noti_email
        saveNotiEmails(allForms, "PENDING");
    }

    private void saveNotiEmails(List<BorrowForm> borrowForms, String status) {
        borrowForms.forEach(borrowForm -> {
            User user = borrowForm.getUser();
            List<BorrowDetail> borrowDetails = borrowDetailRepository.findByBorrowFormId(borrowForm.getId());

            borrowDetails.forEach(borrowDetail -> {
                Book book = bookRepository.findById(borrowDetail.getBookId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
                Fine fine = fineRepository.findByBorrowRecordId(borrowForm.getId());

                NotiEmail notiEmail = new NotiEmail();
                notiEmail.setUserId(user.getId());
                notiEmail.setBorrowRecordId(borrowForm.getId());
                notiEmail.setEmail(user.getEmail());
                notiEmail.setBookTitle(book.getTitle());
                notiEmail.setDueDate(convertToLocalDate(borrowForm.getDueDate()));
                notiEmail.setPenaltyAmount(fine != null ? fine.getPenaltyAmount().doubleValue() : 0);
                notiEmail.setStatus(status);
                notiEmail.setSendDate(null);

                notiEmailRepository.save(notiEmail);
            });
        });
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
