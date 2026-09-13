package com.airesumeanalyzer.backend.processing.dto;

import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.enums.ProcessingJobStatus;
import com.airesumeanalyzer.backend.processing.enums.ProcessingStage;

import java.time.Instant;
import java.util.UUID;

public record ProcessingJobResponse(
        UUID jobId,
        UUID documentId,
        DocumentType documentType,
        ProcessingJobStatus status,
        ProcessingStage currentStage,
        Integer progress,
        Integer retryCount,
        Instant startedAt,
        Instant finishedAt,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {}