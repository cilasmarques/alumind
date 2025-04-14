package com.alura.alumind.controller;

import com.alura.alumind.dto.ReportResponse;
import com.alura.alumind.service.EmailService;
import com.alura.alumind.service.LLMService;
import com.alura.alumind.service.ReportService;
import com.alura.alumind.utils.LLMPrompts;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for generating and retrieving feedback reports
 * 
 * This controller provides endpoints for accessing aggregated feedback reports
 * including statistics and trending feature requests.
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final EmailService emailService;
    private final LLMService llmService;

    /**
     * Generate and retrieve a current feedback report
     * 
     * @return A report containing feedback statistics and top requested features
     */
    @GetMapping
    public ResponseEntity<ReportResponse> getReport() {
        return ResponseEntity.ok(reportService.generateReport());
    }

    /**
     * WARNING: This endpoint is for testing purposes only!
     * 
     * This method is exclusively used for email sending tests
     * during development. It should not be used in a production environment.
     * 
     * Simulates the generation and sending of a weekly report, using the current
     * date as a reference for the report period (from Monday to Sunday of the current
     * week).
     * 
     * @return ResponseEntity<Void> with status 200 if the email is successfully sent
     */
    @GetMapping("/sendEmail")
    public ResponseEntity<Void> sendEmail() {
        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime endOfWeek = LocalDateTime.now().with(DayOfWeek.SUNDAY).with(LocalTime.MAX);
        ReportResponse reportData = reportService.generateWeeklyReport(startOfWeek, endOfWeek);

        String prompt = String.format(
                LLMPrompts.WEEKLY_REPORT_PROMPT,
                startOfWeek.toLocalDate() + " - " + endOfWeek.toLocalDate(),
                reportData.getStatistics().getTotalFeedbacks(),
                reportData.getStatistics().getPercentPositive(),
                reportData.getStatistics().getPercentNegative(),
                reportData.getTopFeatures());

        String emailContent = llmService.sendPrompt(prompt);

        String reportPeriod = startOfWeek.toLocalDate() + " - " + endOfWeek.toLocalDate();
        String subject = "AluMind - Relatório Semanal de Feedbacks (" + reportPeriod + ")";
        emailService.sendEmail(subject, emailContent);

        return ResponseEntity.ok().build();
    }
}