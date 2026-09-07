package com.airesumeanalyzer.backend.recommendation.model;

import java.util.List;

public record RecommendationResponse(
        String overallAssessment,
        List<String> strengths,
        List<String> criticalGaps,
        List<String> recommendations
) {
}