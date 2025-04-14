package com.alura.alumind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AluMind Application - Feedback Management System for mental health applications
 * 
 * This application collects and analyzes user feedback using AI to extract sentiments
 * and feature requests. It also provides reports and statistics on user feedback.
 * 
 * Key features:
 * - Feedback collection and analysis
 * - Sentiment classification (positive/negative/inconclusive)
 * - Spam detection
 * - Feature request extraction
 * - Reporting and statistics
 * - Weekly email reports
 */
@SpringBootApplication
@EnableScheduling
public class AlumindApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlumindApplication.class, args);
    }
}