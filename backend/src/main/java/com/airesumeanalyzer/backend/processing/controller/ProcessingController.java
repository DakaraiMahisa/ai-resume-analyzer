package com.airesumeanalyzer.backend.processing.controller;

import com.airesumeanalyzer.backend.common.api.ApiResponse;
import com.airesumeanalyzer.backend.common.api.ApiRoutes;
import com.airesumeanalyzer.backend.processing.dto.ProcessingJobResponse;
import com.airesumeanalyzer.backend.processing.entity.ProcessingJob;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.mapper.ProcessingJobMapper;
import com.airesumeanalyzer.backend.processing.service.DocumentProcessingService;
import com.airesumeanalyzer.backend.processing.service.ProcessingJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.PROCESSING)
@RequiredArgsConstructor
public class ProcessingController {

    private final DocumentProcessingService documentProcessingService;
    private final ProcessingJobService processingJobService;
    private final ProcessingJobMapper processingJobMapper;

    @GetMapping("/jobs/{documentType}/{documentId}")
    public ResponseEntity<ApiResponse<ProcessingJobResponse>> getLatestJob(
            @PathVariable DocumentType documentType,
            @PathVariable UUID documentId
    ) {
        ProcessingJob job =
                processingJobService.getLatestJob(
                        documentType,
                        documentId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        processingJobMapper.toResponse(job)
                )
        );
    }
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