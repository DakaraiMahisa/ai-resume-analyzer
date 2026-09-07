package com.airesumeanalyzer.backend.analysis.service;

import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.ats.service.ATSScoringService;
import com.airesumeanalyzer.backend.matching.service.ResumeJobMatchingService;
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

    @Transactional(readOnly = true)
    public ATSResult analyze(
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

        return atsScoringService.score(
                requirementResults
        );
    }
}