package com.airesumeanalyzer.backend.processing.controller;

import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import com.airesumeanalyzer.backend.processing.service.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.PROCESSING)
@RequiredArgsConstructor
public class ProcessingController {

    private final DocumentProcessingService documentProcessingService;

    @PostMapping("/jobs/{jobId}/process")
    public ResponseEntity<ApiResponse<Void>> process(
            @PathVariable UUID jobId
    ) {
        documentProcessingService.process(jobId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Document processing completed successfully"
                )
        );
    }
}