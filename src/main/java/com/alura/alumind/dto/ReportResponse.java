package com.alura.alumind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private StatisticsDto statistics;
    private List<TopFeaturesDto> topFeatures;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    @SqlResultSetMapping(name = "RFFullDtoMapping", classes = @ConstructorResult(targetClass = TopFeaturesDto.class, columns = {
            @ColumnResult(name = "code", type = String.class),
            @ColumnResult(name = "feedbackIds", type = String.class),
            @ColumnResult(name = "feedbacksCounter", type = Long.class)
    }))
    public static class TopFeaturesDto {        
        private String code;
        private String feedbackIds;        
        private Long feedbacksCounter;
        
        @JsonProperty("feedbackIds")
        public List<Long> getFeedbackIdsList() {
            if (feedbackIds == null || feedbackIds.isEmpty()) {
                return Collections.emptyList();
            }
            return Arrays.stream(feedbackIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }
    }
}