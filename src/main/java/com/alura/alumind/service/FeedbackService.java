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

/**
 * Service for handling user feedback processing and analysis
 * 
 * This service is responsible for:
 * - Processing incoming feedback
 * - Checking for spam or inappropriate content
 * - Analyzing feedback sentiment using AI
 * - Extracting feature requests from feedback
 * - Persisting feedback and analysis results
 * - Retrieving feedback information
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final LLMService llmService;

    /// ======= Public methods ======= ///

    /**
     * Analyze and store user feedback
     * 
     * This method:
     * 1. Checks for spam or inappropriate content
     * 2. Analyzes feedback sentiment using the LLM
     * 3. Extracts feature requests
     * 4. Saves the feedback and analysis results to the database
     * 
     * @param request The feedback request containing user feedback text
     * @return A DTO with feedback analysis results
     * @throws IllegalArgumentException if the content is classified as spam
     */
    @Transactional
    public FeedbackShortDto analyzeFeedback(FeedbackRequest request) {
        String content = request.getFeedback();
        validateContent(content);
        checkSpam(content);

        JsonNode LLMAnalysis = analyzeWithLLM(content);
        Feedback feedback = buildFeedback(content, LLMAnalysis);
        Feedback saved = feedbackRepository.save(feedback);

        return toShortDto(saved);
    }

    /**
     * Retrieve detailed feedback information by ID
     * 
     * @param id The unique identifier of the feedback
     * @return A DTO with complete feedback information
     * @throws ResponseStatusException if the feedback is not found
     */
    public FeedbackFullDto getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found"));

        return toFullDto(feedback);
    }

    /// ======= Private methods ======= ///

    /**
     * Validate feedback content
     * 
     * Checks if the content is empty or too short
     * 
     * @param content The feedback content to validate
     * @throws IllegalArgumentException if the content is invalid
     */
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Feedback content cannot be empty");
        }
    }   

    /**
     * Check if the feedback content is spam or inappropriate
     * 
     * Uses an LLM to analyze the content and determine if it should be rejected
     * 
     * @param content The feedback content to check
     * @throws IllegalArgumentException if the content is classified as spam
     */
    private void checkSpam(String content) {
        try {
            String prompt = String.format(LLMPrompts.SPAM_ANALYSIS_PROMPT, content);
            JsonNode result = llmService.sendPromptAndParseJson(prompt);

            if (result.has("isSpam") && result.get("isSpam").asBoolean()) {
                log.warn("Spam detected: {}", content);
                throw new IllegalArgumentException("Content classified as spam");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error checking spam: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error validating content", e);
        }
    }

    /**
     * Analyze feedback content using the LLM
     * 
     * Sends the feedback to the LLM for sentiment analysis and feature extraction
     * 
     * @param content The feedback content to analyze
     * @return A JsonNode containing the analysis results
     */
    private JsonNode analyzeWithLLM(String content) {
        try {
            String prompt = String.format(LLMPrompts.FEEDBACK_ANALYSIS_PROMPT, content);
            return llmService.sendPromptAndParseJson(prompt);
        } catch (Exception e) {
            log.error("LLM analysis error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "LLM analysis failed: " + e.getMessage());
        }
    }

    /**
     * Build a Feedback entity from content and analysis results
     * 
     * @param content The original feedback content
     * @param analysis The LLM analysis results
     * @return A Feedback entity ready to be persisted
     */
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

    /**
     * Convert a Feedback entity to a short DTO
     * 
     * @param feedback The Feedback entity to convert
     * @return A short DTO with basic feedback information
     */
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

    /**
     * Convert a Feedback entity to a full DTO
     * 
     * @param feedback The Feedback entity to convert
     * @return A full DTO with complete feedback information
     */
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