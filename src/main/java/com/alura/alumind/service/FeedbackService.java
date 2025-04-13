package com.alura.alumind.service;

import com.alura.alumind.dto.FeedbackRequest;
import com.alura.alumind.dto.FeedbackResponse;
import com.alura.alumind.model.Feedback;
import com.alura.alumind.model.RequestedFeature;
import com.alura.alumind.repository.FeedbackRepository;
import com.fasterxml.jackson.databind.JsonNode;
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
    private final LLMService llmService;

    @Transactional
    public FeedbackResponse analyzeFeedback(FeedbackRequest request) {
        String feedbackContent = request.getFeedback();
        String prompt = buildFeedbackAnalysisPrompt(feedbackContent);
        JsonNode analysisJson = llmService.sendPromptAndParseJson(prompt);

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
    }
    
    private String buildFeedbackAnalysisPrompt(String feedbackContent) {
        return "Analyze the following user feedback for the AluMind app (a mental health and wellness application):\n"
                + feedbackContent + "\n"
                + "Return the analysis in JSON format with the following structure:\n"
                + "{\n"
                + "  \"sentiment\": \"[POSITIVO/NEGATIVO/INCONCLUSIVO]\",\n"
                + "  \"requested_features\": [\n"
                + "    {\n"
                + "      \"code\": \"[UNIQUE_FEATURE_CODE]\",\n"
                + "      \"reason\": \"[REASON WHY THE FEATURE IS IMPORTANT]\"\n"
                + "    }\n"
                + "  ]\n"
                + "}\n\n"
                + "Rules for analysis:\n"
                + "1. The sentiment must be classified as \"POSITIVO\", \"NEGATIVO\", or \"INCONCLUSIVO\" based on the overall tone of the feedback.\n"
                + "2. Identify possible requested features in the feedback and, for each one, create a unique code in UPPERCASE_WITH_UNDERSCORES format (e.g., \"EDIT_PROFILE\").\n"
                + "3. For each feature, briefly explain why implementing it would be important from the user's perspective.\n"
                + "4. If there are no requested features, return an empty list for \"requested_features\".\n"
                + "5. Ensure the JSON is well-formed and valid.\n"
                + "6. Return the sentiment, code and reason in portuguese.\n";
    }
}