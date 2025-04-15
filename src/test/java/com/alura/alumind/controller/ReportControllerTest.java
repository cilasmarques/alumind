package com.alura.alumind.controller;

import com.alura.alumind.dto.ReportResponse;
import com.alura.alumind.dto.ReportResponse.StatisticsDto;
import com.alura.alumind.dto.ReportResponse.TopFeaturesDto;
import com.alura.alumind.service.EmailService;
import com.alura.alumind.service.LLMService;
import com.alura.alumind.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private LLMService llmService;

    private ReportResponse mockReport;

    @BeforeEach
    void setUp() {
        // Setup mock data
        StatisticsDto statisticsDto = StatisticsDto.builder()
                .totalFeedbacks(10)
                .percentPositive(60.0)
                .percentNegative(30.0)
                .percentInconclusive(10.0)
                .build();

        TopFeaturesDto feature1 = new TopFeaturesDto();
        feature1.setCode("ADICIONAR_NOTIFICACOES");
        feature1.setFeedbackIds("1,2,3");
        feature1.setFeedbacksCounter(3L);

        TopFeaturesDto feature2 = new TopFeaturesDto();
        feature2.setCode("MELHORAR_INTERFACE");
        feature2.setFeedbackIds("4,5");
        feature2.setFeedbacksCounter(2L);

        List<TopFeaturesDto> topFeatures = Arrays.asList(feature1, feature2);

        mockReport = ReportResponse.builder()
                .statistics(statisticsDto)
                .topFeatures(topFeatures)
                .build();
    }

    @Test
    void getReport_ValidRequest_ReturnsReport() throws Exception {
        // Arrange
        when(reportService.generateReport()).thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statistics.totalFeedbacks").value(10))
                .andExpect(jsonPath("$.statistics.percentPositive").value(60.0))
                .andExpect(jsonPath("$.statistics.percentNegative").value(30.0))
                .andExpect(jsonPath("$.statistics.percentInconclusive").value(10.0))
                .andExpect(jsonPath("$.topFeatures[0].code").value("ADICIONAR_NOTIFICACOES"))
                .andExpect(jsonPath("$.topFeatures[0].feedbacksCounter").value(3))
                .andExpect(jsonPath("$.topFeatures[0].feedbackIds").isArray())
                .andExpect(jsonPath("$.topFeatures[0].feedbackIds[0]").value(1))
                .andExpect(jsonPath("$.topFeatures[0].feedbackIds[1]").value(2))
                .andExpect(jsonPath("$.topFeatures[0].feedbackIds[2]").value(3))
                .andExpect(jsonPath("$.topFeatures[1].code").value("MELHORAR_INTERFACE"))
                .andExpect(jsonPath("$.topFeatures[1].feedbacksCounter").value(2))
                .andExpect(jsonPath("$.topFeatures[1].feedbackIds").isArray())
                .andExpect(jsonPath("$.topFeatures[1].feedbackIds[0]").value(4))
                .andExpect(jsonPath("$.topFeatures[1].feedbackIds[1]").value(5));
    }

    @Test
    void sendEmail_ValidRequest_SendsEmailAndReturnsOk() throws Exception {
        // Arrange
        String htmlContent = "<html><body><h1>Weekly Report</h1></body></html>";

        when(reportService.generateWeeklyReport(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockReport);
        when(llmService.sendPrompt(anyString())).thenReturn(htmlContent);

        // Act & Assert
        mockMvc.perform(get("/reports/sendEmail"))
                .andExpect(status().isOk());

        verify(reportService).generateWeeklyReport(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(llmService).sendPrompt(anyString());
        verify(emailService).sendEmail(anyString(), anyString());
    }
}