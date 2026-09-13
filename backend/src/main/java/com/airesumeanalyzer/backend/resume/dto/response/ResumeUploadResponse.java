package com.airesumeanalyzer.backend.resume.dto.response;

import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;

import java.time.Instant;
import java.util.UUID;

public record ResumeUploadResponse(
        UUID resumeId,
        UUID processingJobId,
        DocumentProcessingStatus status,
        Instant uploadedAt
) {
}