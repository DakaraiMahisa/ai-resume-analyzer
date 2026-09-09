package com.airesumeanalyzer.backend.analysis.model;

import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.recommendation.model.RecommendationResponse;

import java.util.Objects;

public record ResumeAnalysisResponse(
        ATSResult ats,
        RecommendationResponse recommendation
) {

    public ResumeAnalysisResponse {
        Objects.requireNonNull(
                ats,
                "ats must not be null"
        );

        Objects.requireNonNull(
                recommendation,
                "recommendation must not be null"
        );
    }
}