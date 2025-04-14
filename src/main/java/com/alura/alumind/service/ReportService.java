package com.alura.alumind.service;

import com.alura.alumind.dto.ReportResponse;
import com.alura.alumind.dto.ReportResponse.StatisticsDto;
import com.alura.alumind.dto.ReportResponse.TopFeaturesDto;
import com.alura.alumind.model.Feedback.SentimentType;
import com.alura.alumind.repository.FeedbackRepository;
import com.alura.alumind.repository.RequestedFeaturesRepository;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for generating feedback reports and statistics
 * 
 * This service is responsible for:
 * - Generating current feedback statistics
 * - Creating weekly reports with sentiment analysis
 * - Aggregating top requested features from users
 * - Computing percentages for different sentiment categories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final FeedbackRepository feedbackRepository;
    private final RequestedFeaturesRepository requestedFeatureRepository;

    /// ======= Public methods ======= ///

    /**
     * Generate a comprehensive report of all feedback
     * 
     * This method collects statistics on all feedback in the system including:
     * - Total number of feedback entries
     * - Percentage breakdown by sentiment (positive/negative/inconclusive)
     * - Top requested features with their reasons
     * 
     * @return A ReportResponse DTO containing statistics and top features
     * @throws RuntimeException if an error occurs during report generation
     */
    public ReportResponse generateReport() {
        try {
            long totalFeedbacks = feedbackRepository.count();
            long posCount = feedbackRepository.countBySentiment(SentimentType.POSITIVO);
            long negCount = feedbackRepository.countBySentiment(SentimentType.NEGATIVO);
            long incCount = feedbackRepository.countBySentiment(SentimentType.INCONCLUSIVO);

            StatisticsDto statistics = computeStatistics(totalFeedbacks, posCount, negCount, incCount);
            List<TopFeaturesDto> topFeatures = requestedFeatureRepository.findRFWithFeedbackIds();

            return ReportResponse.builder()
                    .statistics(statistics)
                    .topFeatures(topFeatures)
                    .build();
        } catch (Exception e) {
            log.error("Error generating report", e);
            throw new RuntimeException("Error generating report", e);
        }
    }

    /**
     * Generate a weekly report for a specific date range
     * 
     * This method is similar to generateReport but limits the data to a specific
     * time period, typically used for weekly reports.
     * 
     * @param startDate The start date for the report period (inclusive)
     * @param endDate The end date for the report period (inclusive)
     * @return A ReportResponse DTO containing statistics and top features for the period
     * @throws RuntimeException if an error occurs during report generation
     */
    public ReportResponse generateWeeklyReport(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            long totalFeedbacks = feedbackRepository.countByDate(startDate, endDate);
            long posCount = feedbackRepository.countByDateAndSentiment(startDate, endDate, SentimentType.POSITIVO);
            long negCount = feedbackRepository.countByDateAndSentiment(startDate, endDate, SentimentType.NEGATIVO);
            long incCount = feedbackRepository.countByDateAndSentiment(startDate, endDate,
                    SentimentType.INCONCLUSIVO);

            StatisticsDto statistics = computeStatistics(totalFeedbacks, posCount, negCount, incCount);
            List<TopFeaturesDto> topFeatures = requestedFeatureRepository.findRFWithFeedbackIdsForDateRange(startDate, endDate);

            return ReportResponse.builder()
                    .statistics(statistics)
                    .topFeatures(topFeatures)
                    .build();
        } catch (Exception e) {
            log.error("Error generating weekly report", e);
            throw new RuntimeException("Error generating weekly report", e);
        }
    }

    /// ======= Private methods ======= ///

    /**
     * Compute statistics based on feedback counts
     * 
     * Calculates percentage breakdowns for different sentiment categories.
     * 
     * @param totalFeedbacks Total number of feedbacks
     * @param posCount Count of positive feedbacks
     * @param negCount Count of negative feedbacks
     * @param incCount Count of inconclusive feedbacks
     * @return A StatisticsDto containing the calculated statistics
     */
    private StatisticsDto computeStatistics(long totalFeedbacks, long posCount, long negCount, long incCount) {
        double percentPositive = computePercentage(posCount, totalFeedbacks);
        double percentNegative = computePercentage(negCount, totalFeedbacks);
        double percentInconclusive = computePercentage(incCount, totalFeedbacks);

        return StatisticsDto.builder()
                .totalFeedbacks(totalFeedbacks)
                .percentPositive(percentPositive)
                .percentNegative(percentNegative)
                .percentInconclusive(percentInconclusive)
                .build();
    }

    /**
     * Compute percentage safely
     * 
     * Calculates a percentage while avoiding division by zero.
     * 
     * @param count The numerator (part)
     * @param total The denominator (whole)
     * @return The percentage value, or 0 if total is 0
     */
    private double computePercentage(long count, long total) {
        return total > 0 ? (double) count / total * 100 : 0;
    }
}