package com.example.demo.service;

import com.example.demo.model.NotiEmail;

import java.util.List;

public interface EmailService {

    void sendEmailWithRetry(String to, String subject, String text, List<NotiEmail> notiEmails);


    void sendEmailsFromNotiEmail();

    String buildEmailContent(List<NotiEmail> notiEmails);

    void sendEmail(String to, String subject, String text);

}
