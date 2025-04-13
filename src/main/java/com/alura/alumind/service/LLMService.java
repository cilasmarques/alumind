package com.alura.alumind.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LLMService {

    public String analyzeFeedback(String feedback) {
        log.info("Analyzing feedback: {}", feedback);
        
        // In a real implementation, this would call an actual LLM API
        // For demonstration purposes, we'll generate a mock response based on the feedback content
        
        String sentiment = determineSentiment(feedback);
        String features = extractFeatures(feedback);
        
        return String.format("""
                {
                  "sentiment": "%s",
                  %s
                }
                """, sentiment, features);
    }
    
    private String determineSentiment(String feedback) {
        String lowerFeedback = feedback.toLowerCase();
        
        if (lowerFeedback.contains("gosto") || 
            lowerFeedback.contains("ótimo") || 
            lowerFeedback.contains("excelente") || 
            lowerFeedback.contains("adoro") || 
            lowerFeedback.contains("ajudando")) {
            return "POSITIVO";
        } else 
        if (lowerFeedback.contains("não gosto") || 
                   lowerFeedback.contains("ruim") || 
                   lowerFeedback.contains("péssimo") || 
                   lowerFeedback.contains("horrível") || 
                   lowerFeedback.contains("problema")) {
            return "NEGATIVO";
        } else {
            return "INCONCLUSIVO";
        }
    }
    
    private String extractFeatures(String feedback) {
        if (feedback == null) {
            return "\"requested_features\": []";
        }
        String lowerFeedback = feedback.toLowerCase();
        StringBuilder features = new StringBuilder();
        features.append("\"requested_features\": [");
        
        if (lowerFeedback.contains("edição") || lowerFeedback.contains("editar") || lowerFeedback.contains("perfil")) {
            features.append("""
                    {
                      "code": "EDITAR_PERFIL",
                      "reason": "O usuário gostaria de realizar a edição do próprio perfil"
                    }
                    """);
        }
        
        if (lowerFeedback.contains("notificação") || lowerFeedback.contains("notificar") || lowerFeedback.contains("alerta")) {
            if (features.toString().endsWith("}")) {
                features.append(",");
            }
            features.append("""
                    {
                      "code": "NOTIFICACOES",
                      "reason": "O usuário gostaria de receber notificações sobre novos conteúdos"
                    }
                    """);
        }
        
        if (lowerFeedback.contains("tema") || lowerFeedback.contains("escuro") || lowerFeedback.contains("dark")) {
            if (features.toString().endsWith("}")) {
                features.append(",");
            }
            features.append("""
                    {
                      "code": "TEMA_ESCURO",
                      "reason": "O usuário gostaria de ter um tema escuro no aplicativo"
                    }
                    """);
        }
        
        features.append("]");
        return features.toString();
    }    
}