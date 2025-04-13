package com.alura.alumind.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackRequest {
    
    @NotNull(message = "Feedback content cannot be null")
    @NotBlank(message = "Feedback content cannot be blank")
    @Size(max = 2000, message = "Feedback content cannot exceed 2000 characters")
    private String feedback;
}