package com.alura.alumind.controller;

import com.alura.alumind.dto.FeedbackRequest;
import com.alura.alumind.dto.FeedbackResponse.FeedbackFullDto;
import com.alura.alumind.dto.FeedbackResponse.FeedbackShortDto;
import com.alura.alumind.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackShortDto> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        FeedbackShortDto response = feedbackService.analyzeFeedback(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackFullDto> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }
}