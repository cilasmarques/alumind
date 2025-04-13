package com.alura.alumind.dto;

import com.alura.alumind.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private Long id;
    private String sentiment;
    private List<FeatureDto> requested_features;

    public static FeedbackResponse fromEntity(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .sentiment(feedback.getSentiment().name())
                .requested_features(
                        feedback.getRequestedFeatures().stream()
                                .map(rf -> new FeatureDto(rf.getCode(), rf.getReason()))
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureDto {
        private String code;
        private String reason;
    }
}