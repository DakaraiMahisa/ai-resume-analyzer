package com.airesumeanalyzer.backend.jobdescription.dto.response;

import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;

import java.time.Instant;
import java.util.UUID;

public record JobDescriptionUploadResponse(
        UUID jobDescriptionId,
        DocumentProcessingStatus status,
        Instant uploadedAt
) {
}