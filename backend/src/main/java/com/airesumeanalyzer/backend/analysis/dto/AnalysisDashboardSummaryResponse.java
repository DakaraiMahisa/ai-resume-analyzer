package com.airesumeanalyzer.backend.analysis.dto;

public record AnalysisDashboardSummaryResponse(
        long totalAnalyses,
        long completedAnalyses,
        double averageOverallScore
) {
}
