package com.airesumeanalyzer.backend.analysis.dto;

import com.airesumeanalyzer.backend.analysis.enums.AnalysisStatus;

import java.time.Instant;
import java.util.UUID;

public record AnalysisSummaryResponse(
        UUID analysisId,
        UUID resumeId,
        String resumeDisplayName,
        UUID jobDescriptionId,
        String jobDescriptionDisplayName,
        AnalysisStatus status,
        double overallScore,
        Instant completedAt,
        Instant createdAt
) {
}
