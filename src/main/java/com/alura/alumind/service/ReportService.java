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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final FeedbackRepository feedbackRepository;
    private final RequestedFeaturesRepository requestedFeatureRepository;

    /// ======= Public methods ======= ///

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

    /// ======= Private methods ======= ///

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

    private double computePercentage(long count, long total) {
        return total > 0 ? (double) count / total * 100 : 0;
    }
}