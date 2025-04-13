package com.alura.alumind.service;

import com.alura.alumind.dto.FeedbackRequest;
import com.alura.alumind.dto.FeedbackResponse;
import com.alura.alumind.model.Feedback;
import com.alura.alumind.model.RequestedFeature;
import com.alura.alumind.repository.FeedbackRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ObjectMapper objectMapper;
    private final LLMService llmService;

    @Transactional
    public FeedbackResponse analyzeFeedback(FeedbackRequest request) {
        String feedbackContent = request.getFeedback();
        String aiResponse = llmService.analyzeFeedback(feedbackContent);

        try {
            // Convert AI response to JSON
            JsonNode analysisJson = objectMapper.readTree(aiResponse);
            
            Feedback feedback = Feedback.builder()
                    .content(feedbackContent)
                    .sentiment(Feedback.SentimentType.valueOf(analysisJson.get("sentiment").asText()))
                    .createdAt(LocalDateTime.now())
                    .build();
            
            JsonNode featuresNode = analysisJson.get("requested_features");
            if (featuresNode != null && featuresNode.isArray()) {
                for (JsonNode featureNode : featuresNode) {
                    RequestedFeature feature = RequestedFeature.builder()
                            .code(featureNode.get("code").asText())
                            .reason(featureNode.get("reason").asText())
                            .build();
                    feedback.addRequestedFeature(feature);
                }
            }
            
            Feedback savedFeedback = feedbackRepository.save(feedback);
            return FeedbackResponse.fromEntity(savedFeedback);
            
        } catch (JsonProcessingException e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            throw new RuntimeException("Error analyzing feedback", e);
        }
    }
}