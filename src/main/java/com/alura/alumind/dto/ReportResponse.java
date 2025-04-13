package com.alura.alumind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private StatisticsDto statistics;
    private List<TopFeatureDto> topFeatures;
    private List<FeedbackSummaryDto> recentFeedbacks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsDto {
        private long totalFeedbacks;
        private double percentPositive;
        private double percentNegative;
        private double percentInconclusive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopFeatureDto {
        private String featureCode;
        private long count;
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackSummaryDto {
        private Long id;
        private String content;
        private String sentiment;
        private List<String> features;
    }
}