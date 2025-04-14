package com.alura.alumind.controller;

import com.alura.alumind.dto.ReportResponse;
import com.alura.alumind.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for generating and retrieving feedback reports
 * 
 * This controller provides endpoints for accessing aggregated feedback reports
 * including statistics and trending feature requests.
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Generate and retrieve a current feedback report
     * 
     * @return A report containing feedback statistics and top requested features
     */
    @GetMapping
    public ResponseEntity<ReportResponse> getReport() {
        return ResponseEntity.ok(reportService.generateReport());
    }
}