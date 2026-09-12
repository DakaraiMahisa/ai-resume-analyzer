package com.airesumeanalyzer.backend.analysis.model;

import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.jobdescription.dto.JobDescriptionContext;
import com.airesumeanalyzer.backend.recommendation.model.RecommendationResponse;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ResumeAnalysisResponse(
        UUID analysisId,
        UUID resumeId,
        JobDescriptionContext jobDescription,
        List<RequirementMatchResult> requirementResults,
        ATSResult ats,
        RecommendationResponse recommendation
) {

    public ResumeAnalysisResponse {
        Objects.requireNonNull(
                analysisId,
                "analysisId must not be null"
        );
        Objects.requireNonNull(
                resumeId,
                "resumeId must not be null"
        );
        Objects.requireNonNull(
                requirementResults,
                "requirementResults must not be null"
        );

        Objects.requireNonNull(
                ats,
                "ats must not be null"
        );

        Objects.requireNonNull(
                recommendation,
                "recommendation must not be null"
        );

        requirementResults = List.copyOf(requirementResults);
    }

}