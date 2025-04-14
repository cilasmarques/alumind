package com.alura.alumind.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackShortDto {
        private Long id;
        private String sentiment;
        private List<RequestedFeatures> requestedFeatures;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackFullDto {
        private Long id;
        private String content;
        private String sentiment;
        private String createdAt;
        private List<RequestedFeatures> requestedFeatures;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestedFeatures {
        private String code;
        private String reason;
    }
}