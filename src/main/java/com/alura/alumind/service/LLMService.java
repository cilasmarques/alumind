package com.alura.alumind.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMService {

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    /**
     * Send a prompt to the OpenAI model and get the response
     *
     * @param promptContent The content of the prompt to send
     * @return The raw text response from the model
     */
    public String sendPrompt(String promptContent) {
        ChatResponse aiResponse = chatModel.call(new Prompt(promptContent));
        
        return aiResponse
                .getResult()
                .getOutput()
                .getText()
                .replaceAll("(?s)```json\\s*", "")
                .replaceAll("```", "")
                .trim();
    }
    
    /**
     * Send a prompt to the OpenAI model and parse the response as JSON
     *
     * @param promptContent The content of the prompt to send
     * @return The parsed JSON response
     * @throws RuntimeException if the response cannot be parsed as JSON
     */
    public JsonNode sendPromptAndParseJson(String promptContent) {
        String responseContent = sendPrompt(promptContent);
        
        try {
            return objectMapper.readTree(responseContent);
        } catch (JsonProcessingException e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            throw new RuntimeException("Error parsing AI response", e);
        }
    }
}