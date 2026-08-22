package com.airesumeanalyzer.backend.resume.dto.response;

import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;

import java.time.Instant;
import java.util.UUID;

public record ResumeSummaryResponse(
        UUID id,
        String fileName,
        DocumentProcessingStatus status,
        Instant uploadedAt
) {
}