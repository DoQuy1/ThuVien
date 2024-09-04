package com.example.demo.controller;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 12 * * ?")
    @PostMapping("/send")
    public String sendEmail() {
        try {
            emailService.sendEmailsFromNotiEmail();
            return "Email đã được gửi thành công!";
        } catch (Exception e) {
            return "Gửi email thất bại.";
        }
    }
}
