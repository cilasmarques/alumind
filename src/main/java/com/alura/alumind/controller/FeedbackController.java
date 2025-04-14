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

/**
 * REST controller for handling user feedback operations
 * 
 * This controller provides endpoints for submitting new feedback and retrieving existing feedback.
 * Feedback is analyzed using AI to extract sentiment and feature requests.
 */
@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Submit and analyze new user feedback
     * 
     * @param request The feedback request containing user feedback text
     * @return A DTO with feedback analysis results including sentiment and any extracted feature requests
     */
    @PostMapping
    public ResponseEntity<FeedbackShortDto> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        FeedbackShortDto response = feedbackService.analyzeFeedback(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve detailed information about a specific feedback by ID
     * 
     * @param id The unique identifier of the feedback
     * @return A DTO with complete feedback information
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackFullDto> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }
}