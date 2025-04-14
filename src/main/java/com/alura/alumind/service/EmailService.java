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

/**
 * Service for sending emails from the application
 * 
 * This service handles email composition and sending using Spring's JavaMailSender.
 * The service automatically configures email sender and recipients from application properties.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("#{'${email.weekly-report.recipients}'.split(',')}")
    private List<String> reportRecipients;
    
    /**
     * Send an HTML-formatted email to configured recipients
     * 
     * This method creates and sends an HTML email with the specified subject
     * to the recipients configured in application.properties. Multiple recipients
     * can be specified by separating email addresses with commas.
     * 
     * @param subject The email subject line
     * @param content The HTML-formatted content of the email
     * @throws RuntimeException if the email fails to send
     */
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