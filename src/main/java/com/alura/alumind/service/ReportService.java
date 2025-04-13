package com.alura.alumind.service;

import com.alura.alumind.dto.ReportResponse;
import com.alura.alumind.model.Feedback;
import com.alura.alumind.model.RequestedFeature;
import com.alura.alumind.repository.FeedbackRepository;
import com.alura.alumind.repository.RequestedFeaturesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final FeedbackRepository feedbackRepository;
    private final RequestedFeaturesRepository requestedFeatureRepository;

    public ReportResponse generateReport() {
        long totalFeedbacks = feedbackRepository.count();
        long positiveCount = feedbackRepository.countBySentiment(Feedback.SentimentType.POSITIVO);
        long negativeCount = feedbackRepository.countBySentiment(Feedback.SentimentType.NEGATIVO);
        long inconclusiveCount = feedbackRepository.countBySentiment(Feedback.SentimentType.INCONCLUSIVO);

        double percentPositive = totalFeedbacks > 0 ? (double) positiveCount / totalFeedbacks * 100 : 0;
        double percentNegative = totalFeedbacks > 0 ? (double) negativeCount / totalFeedbacks * 100 : 0;
        double percentInconclusive = totalFeedbacks > 0 ? (double) inconclusiveCount / totalFeedbacks * 100 : 0;

        ReportResponse.StatisticsDto statistics = ReportResponse.StatisticsDto.builder()
                .totalFeedbacks(totalFeedbacks)
                .percentPositive(Math.round(percentPositive * 100.0) / 100.0)
                .percentNegative(Math.round(percentNegative * 100.0) / 100.0)
                .percentInconclusive(Math.round(percentInconclusive * 100.0) / 100.0)
                .build();

        // Get top requested features
        List<Object[]> topFeaturesData = requestedFeatureRepository.findMostRequestedFeatures();
        List<ReportResponse.TopFeatureDto> topFeatures = new ArrayList<>();
        
        Map<String, String> featureReasons = new HashMap<>();
        List<Feedback> allFeedbacks = feedbackRepository.findAll();
        for (Feedback feedback : allFeedbacks) {
            for (RequestedFeature feature : feedback.getRequestedFeatures()) {
                featureReasons.put(feature.getCode(), feature.getReason());
            }
        }

        for (Object[] data : topFeaturesData) {
            String featureCode = (String) data[0];
            Long count = ((Number) data[1]).longValue();
            String reason = featureReasons.getOrDefault(featureCode, "No reason provided");
            
            topFeatures.add(new ReportResponse.TopFeatureDto(featureCode, count, reason));
        }

        return ReportResponse.builder()
                .statistics(statistics)
                .topFeatures(topFeatures)
                .build();
    }
}