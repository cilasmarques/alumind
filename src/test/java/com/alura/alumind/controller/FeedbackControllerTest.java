package com.alura.alumind.controller;

import com.alura.alumind.dto.FeedbackRequest;
import com.alura.alumind.dto.FeedbackResponse.FeedbackFullDto;
import com.alura.alumind.dto.FeedbackResponse.FeedbackShortDto;
import com.alura.alumind.dto.FeedbackResponse.RequestedFeatures;
import com.alura.alumind.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FeedbackControllerTest {

    private MockMvc mockMvc;
    private FeedbackService feedbackService;
    private ObjectMapper objectMapper;

    private FeedbackShortDto mockShortDto;
    private FeedbackFullDto mockFullDto;

    @BeforeEach
    void setUp() {
        feedbackService = mock(FeedbackService.class);
        objectMapper = new ObjectMapper();
        FeedbackController feedbackController = new FeedbackController(feedbackService);
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController).build();

        // Setup mock data
        RequestedFeatures feature = RequestedFeatures.builder()
                .code("ADICIONAR_NOTIFICACOES")
                .reason("Melhorar engajamento do usuário")
                .build();

        mockShortDto = FeedbackShortDto.builder()
                .id(1L)
                .sentiment("POSITIVO")
                .requestedFeatures(List.of(feature))
                .build();

        mockFullDto = FeedbackFullDto.builder()
                .id(1L)
                .content("Adorei o aplicativo, mas seria bom ter notificações")
                .sentiment("POSITIVO")
                .createdAt("2023-05-10T15:30:00")
                .requestedFeatures(List.of(feature))
                .build();
    }

    @Test
    void submitFeedback_ValidRequest_ReturnsOk() throws Exception {
        FeedbackRequest request = new FeedbackRequest();
        request.setFeedback("Adorei o aplicativo, mas seria bom ter notificações");
        when(feedbackService.analyzeFeedback(any(FeedbackRequest.class))).thenReturn(mockShortDto);

        mockMvc.perform(post("/feedbacks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sentiment").value("POSITIVO"))
                .andExpect(jsonPath("$.requestedFeatures[0].code").value("ADICIONAR_NOTIFICACOES"))
                .andExpect(jsonPath("$.requestedFeatures[0].reason").value("Melhorar engajamento do usuário"));
    }

    @Test
    void getFeedbackById_ExistingId_ReturnsOk() throws Exception {
        Long feedbackId = 1L;
        when(feedbackService.getFeedbackById(feedbackId)).thenReturn(mockFullDto);

        mockMvc.perform(get("/feedbacks/{id}", feedbackId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Adorei o aplicativo, mas seria bom ter notificações"))
                .andExpect(jsonPath("$.sentiment").value("POSITIVO"))
                .andExpect(jsonPath("$.createdAt").value("2023-05-10T15:30:00"))
                .andExpect(jsonPath("$.requestedFeatures[0].code").value("ADICIONAR_NOTIFICACOES"));
    }

    @Test
    void getFeedbackById_NonExistingId_ReturnsNotFound() throws Exception {
        Long feedbackId = 999L;
        when(feedbackService.getFeedbackById(anyLong()))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Feedback not found"));

        mockMvc.perform(get("/feedbacks/{id}", feedbackId))
                .andExpect(status().isNotFound());
    }
}