package com.alura.alumind.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alura.alumind.dto.ReportResponse;
import com.alura.alumind.service.EmailService;
import com.alura.alumind.service.LLMService;
import com.alura.alumind.service.ReportService;
import com.alura.alumind.utils.LLMPrompts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyReportScheduler {

    private final ReportService reportService;
    private final LLMService llmService;
    private final EmailService emailService;

    /// ======= Public methods ======= ///

    @Scheduled(cron = "0 0 8 * * SUN") // Every Sunday at 8:00 AM
    public void sendWeeklyReport() {
        try {
            LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).with(LocalTime.MIN);
            LocalDateTime endOfWeek = LocalDateTime.now().with(DayOfWeek.SUNDAY).with(LocalTime.MAX);
            ReportResponse reportData = reportService.generateWeeklyReport(startOfWeek, endOfWeek);
            String reportPeriod = startOfWeek.toLocalDate() + " - " + endOfWeek.toLocalDate();

            String emailContent = generateEmailContent(reportData, reportPeriod);

            String subject = "AluMind - Relatório Semanal de Feedbacks (" + reportPeriod + ")" ;
            emailService.sendEmail(subject, emailContent);

            log.info("Weekly report sent successfully");
        } catch (Exception e) {
            log.error("Error sending weekly report email", e);
        }
    }

    /// ======= Private methods ======= ///

    private String generateEmailContent(ReportResponse reportData, String reportPeriod) {
        String prompt = String.format(
                LLMPrompts.WEEKLY_REPORT_PROMPT,
                reportPeriod,
                reportData.getStatistics().getTotalFeedbacks(),
                reportData.getStatistics().getPercentPositive(),
                reportData.getStatistics().getPercentNegative(),
                reportData.getTopFeatures());

        return llmService.sendPrompt(prompt);
    }
}