package com.alura.alumind.service;

import com.alura.alumind.dto.FeedbackRequest;
import com.alura.alumind.dto.FeedbackResponse.FeedbackFullDto;
import com.alura.alumind.dto.FeedbackResponse.FeedbackShortDto;
import com.alura.alumind.dto.FeedbackResponse.RequestedFeatures;
import com.alura.alumind.model.Feedback;
import com.alura.alumind.model.RequestedFeature;
import com.alura.alumind.repository.FeedbackRepository;
import com.alura.alumind.utils.LLMPrompts;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final LLMService llmService;

    /// ======= Public methods ======= ///

    @Transactional
    public FeedbackShortDto analyzeFeedback(FeedbackRequest request) {
        String content = request.getFeedback();

        JsonNode LLMAnalysis = analyzeWithLLM(content);
        Feedback feedback = buildFeedback(content, LLMAnalysis);
        Feedback saved = feedbackRepository.save(feedback);

        return toShortDto(saved);
    }

    public FeedbackFullDto getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found"));

        return toFullDto(feedback);
    }

    /// ======= Private methods ======= ///

    private JsonNode analyzeWithLLM(String content) {
        try {
            String prompt = String.format(LLMPrompts.FEEDBACK_ANALYSIS_PROMPT, content);
            return llmService.sendPromptAndParseJson(prompt);
        } catch (Exception e) {
            log.error("LLM analysis error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "LLM analysis failed: " + e.getMessage());
        }
    }

    private Feedback buildFeedback(String content, JsonNode analysis) {
        Feedback feedback = new Feedback();
        feedback.setContent(content);
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setSentiment(Feedback.SentimentType.valueOf(analysis.get("sentiment").asText()));

        if (analysis.has("requestedFeatures")) {
            for (JsonNode feature : analysis.get("requestedFeatures")) {
                RequestedFeature rf = new RequestedFeature();
                rf.setCode(feature.get("code").asText());
                rf.setReason(feature.get("reason").asText());
                feedback.addRequestedFeature(rf);
            }
        }

        return feedback;
    }

    private FeedbackShortDto toShortDto(Feedback feedback) {
        List<RequestedFeatures> features = feedback.getRequestedFeatures()
                .stream()
                .map(f -> RequestedFeatures.builder()
                        .code(f.getCode())
                        .reason(f.getReason())
                        .build())
                .toList();

        return FeedbackShortDto.builder()
                .id(feedback.getId())
                .sentiment(feedback.getSentiment().name())
                .requestedFeatures(features)
                .build();
    }

    private FeedbackFullDto toFullDto(Feedback feedback) {
        List<RequestedFeatures> features = feedback.getRequestedFeatures()
                .stream()
                .map(f -> RequestedFeatures.builder()
                        .code(f.getCode())
                        .reason(f.getReason())
                        .build())
                .toList();

        return FeedbackFullDto.builder()
                .id(feedback.getId())
                .content(feedback.getContent())
                .sentiment(feedback.getSentiment().name())
                .createdAt(feedback.getCreatedAt().toString())
                .requestedFeatures(features)
                .build();
    }
}