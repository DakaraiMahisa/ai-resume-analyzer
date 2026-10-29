package com.airesumeanalyzer.backend.analysis.dto;

import com.airesumeanalyzer.backend.analysis.enums.AnalysisStatus;
import com.airesumeanalyzer.backend.analysis.model.ResumeAnalysisResponse;

import java.time.Instant;
import java.util.UUID;

public record AnalysisStatusResponse(
        UUID analysisId,
        AnalysisStatus status,
        ResumeAnalysisResponse result,
        AnalysisErrorResponse error,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        Instant updatedAt,
        int attemptNumber
) {
}
