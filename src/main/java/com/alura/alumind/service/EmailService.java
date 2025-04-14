package com.alura.alumind.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("#{'${email.weekly-report.recipients}'.split(',')}")
    private List<String> reportRecipients;
    
    public void sendEmail(String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(reportRecipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(content, true); // true indicates HTML content
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", reportRecipients);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", reportRecipients, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}