package com.example.demo.service.impl;

import com.example.demo.model.NotiEmail;
import com.example.demo.model.User;
import com.example.demo.repository.NotiEmailRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private NotiEmailRepository notiEmailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;

    private static final long EMAIL_RETRY_LIMIT = 3;

    @Override
    public void sendEmailsFromNotiEmail() {
        // Lấy tất cả các bản ghi với trạng thái PENDING
        List<NotiEmail> pendingEmails = notiEmailRepository.findByStatus("PENDING");

        // Nhóm các bản ghi theo user_id để gộp email
        Map<Long, List<NotiEmail>> emailsByUser = pendingEmails.stream()
                .collect(Collectors.groupingBy(NotiEmail::getUserId));

        emailsByUser.forEach((userId, notiEmails) -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            String subject = "Thông Báo Sách Sắp Đến Hạn hoặc Quá Hạn";
            String text = buildEmailContent(notiEmails);

            // Gửi email và cập nhật trạng thái gửi
            sendEmailWithRetry(user.getEmail(), subject, text, notiEmails);
        });
    }

    public void sendEmailWithRetry(String to, String subject, String text, List<NotiEmail> notiEmails) {
        int attempt = 0;
        boolean sent = false;
        LocalDateTime currentTime = LocalDateTime.now();

        while (attempt < EMAIL_RETRY_LIMIT && !sent) {
            try {
                sendEmail(to, subject, text);
                updateNotiEmailsStatus(notiEmails, "DONE", currentTime);
                sent = true;
            } catch (Exception e) {
                attempt++;
                if (attempt >= EMAIL_RETRY_LIMIT) {
                    updateNotiEmailsStatus(notiEmails, "FAIL", null);
                }
            }
        }
    }

    private void updateNotiEmailsStatus(List<NotiEmail> notiEmails, String status, LocalDateTime sendDate) {
        notiEmails.forEach(notiEmail -> {
            notiEmail.setStatus(status);
            notiEmail.setSendDate(sendDate);
        });
        notiEmailRepository.saveAll(notiEmails);
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public String buildEmailContent(List<NotiEmail> notiEmails) {
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Chào bạn,\n\n");

        LocalDate now = LocalDate.now();
        LocalDate warningDate = now.plusDays(3);

        boolean hasOverdueBooks = notiEmails.stream()
                .anyMatch(notiEmail -> now.isAfter(notiEmail.getDueDate()));

        boolean hasUpcomingBooks = notiEmails.stream()
                .anyMatch(notiEmail -> warningDate.isAfter(notiEmail.getDueDate()) && now.isBefore(notiEmail.getDueDate()));

        if (hasOverdueBooks) {
            emailContent.append("Sách bạn mượn đã quá hạn trả:\n");
            notiEmails.stream()
                    .filter(notiEmail -> now.isAfter(notiEmail.getDueDate()))
                    .forEach(notiEmail -> {
                        emailContent.append(" - ").append(notiEmail.getBookTitle())
                                .append(" (Ngày đến hạn: ").append(notiEmail.getDueDate())
                                .append(", Số tiền phạt: ").append(notiEmail.getPenaltyAmount()).append(" VNĐ)\n");
                    });
        }

        if (hasUpcomingBooks) {
            emailContent.append("\nSách bạn mượn sắp đến hạn trả:\n");
            notiEmails.stream()
                    .filter(notiEmail -> warningDate.isAfter(notiEmail.getDueDate()) && now.isBefore(notiEmail.getDueDate()))
                    .forEach(notiEmail -> {
                        emailContent.append(" - ").append(notiEmail.getBookTitle())
                                .append(" (Ngày đến hạn: ").append(notiEmail.getDueDate()).append(")\n");
                    });
        }

        emailContent.append("\nVui lòng kiểm tra và trả sách đúng hạn để tránh bị phạt thêm.\n\n");
        emailContent.append("Thư viện ");

        return emailContent.toString();
    }
}
