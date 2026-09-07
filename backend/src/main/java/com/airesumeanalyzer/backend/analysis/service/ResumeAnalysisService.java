package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.analysis.model.ResumeAnalysisResponse;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.ats.service.ATSScoringService;
import com.airesumeanalyzer.backend.matching.service.ResumeJobMatchingService;
import com.airesumeanalyzer.backend.recommendation.model.RecommendationResponse;
import com.airesumeanalyzer.backend.recommendation.service.RecommendationService;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final ResumeJobMatchingService resumeJobMatchingService;
    private final ATSScoringService atsScoringService;
    private final RecommendationService recommendationService;

    @Transactional(readOnly = true)
    public ResumeAnalysisResponse analyze(
            UUID resumeId,
            UUID jobDescriptionId
    ) {

        Objects.requireNonNull(
                resumeId,
                "resumeId must not be null"
        );

        Objects.requireNonNull(
                jobDescriptionId,
                "jobDescriptionId must not be null"
        );

        List<RequirementMatchResult> requirementResults =
                resumeJobMatchingService.match(
                        resumeId,
                        jobDescriptionId
                );

        ATSResult atsResult =
                atsScoringService.score(
                        requirementResults
                );

        RecommendationResponse recommendation =
                recommendationService.recommend(
                        atsResult
                );

        return new ResumeAnalysisResponse(
                atsResult,
                recommendation
        );
    }
}