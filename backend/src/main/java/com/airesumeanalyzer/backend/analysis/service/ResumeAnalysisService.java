package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.analysis.entity.Analysis;
import com.airesumeanalyzer.backend.analysis.model.ResumeAnalysisResponse;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.ats.service.ATSScoringService;
import com.airesumeanalyzer.backend.jobdescription.dto.JobDescriptionContext;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.jobdescription.repository.JobDescriptionRepository;
import com.airesumeanalyzer.backend.matching.service.ResumeJobMatchingService;
import com.airesumeanalyzer.backend.recommendation.model.RecommendationResponse;
import com.airesumeanalyzer.backend.recommendation.service.RecommendationService;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
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

    private final AnalysisPersistenceService analysisPersistenceService;
    private final AnalysisResultSerializer analysisResultSerializer;

    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;

    @Transactional
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

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Resume not found: " + resumeId
                        )
                );

        JobDescription jobDescription =
                jobDescriptionRepository.findById(jobDescriptionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job description not found: "
                                                + jobDescriptionId
                                )
                        );

        String jobDescriptionDisplayName =
                jobDescription.getOriginalFilename() != null
                        ? jobDescription.getOriginalFilename()
                        : "Pasted job description";

        Analysis analysis =
                analysisPersistenceService.createRunning(
                        resume,
                        jobDescription
                );

        try {

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

            ResumeAnalysisResponse response =
                    new ResumeAnalysisResponse(
                            analysis.getId(),
                            resumeId,
                            new JobDescriptionContext(
                                    jobDescription.getId(),
                                    jobDescriptionDisplayName
                            ),
                            requirementResults,
                            atsResult,
                            recommendation
                    );

            String resultData =
                    analysisResultSerializer.serialize(response);

            analysisPersistenceService.markCompleted(
                    analysis.getId(),
                    atsResult.overallScore(),
                    atsResult.required().score(),
                    atsResult.preferred().score(),
                    resultData
            );

            return response;

        } catch (Exception exception) {

            analysisPersistenceService.markFailed(
                    analysis.getId(),
                    exception.getMessage()
            );

            throw exception;
        }
    }
}
