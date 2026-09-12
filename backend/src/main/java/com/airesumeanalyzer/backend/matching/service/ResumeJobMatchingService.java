package com.airesumeanalyzer.backend.matching.service;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.entity.ClaimStatus;
import com.airesumeanalyzer.backend.ai.domain.repository.ClaimRepository;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.jobdescription.repository.JobDescriptionRepository;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;
import com.airesumeanalyzer.backend.rie.domain.RequirementToEvaluate;
import com.airesumeanalyzer.backend.rie.service.RequirementMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeJobMatchingService {

    private final ClaimRepository claimRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final RequirementMatchingService requirementMatchingService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<RequirementMatchResult> match(
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

        List<Claim> resumeClaims =
                claimRepository.findBySourceDocumentIdAndStatus(
                        resumeId,
                        ClaimStatus.VALIDATED
                );

        List<Claim> requirementClaims =
                claimRepository.findBySourceDocumentIdAndStatus(
                        jobDescriptionId,
                        ClaimStatus.VALIDATED
                );

        JobDescription jobDescription =
                jobDescriptionRepository.findById(jobDescriptionId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Job description not found: "
                                                + jobDescriptionId
                                )
                        );

        StructuredJobDescription structuredJobDescription =
                deserializeStructuredData(
                        jobDescription.getStructuredData()
                );

        List<RequirementToEvaluate> requirements =
                extractRequirements(
                        structuredJobDescription
                );

        return requirementMatchingService.match(
                requirements,
                requirementClaims,
                resumeClaims
        );
    }

    private StructuredJobDescription deserializeStructuredData(
            String structuredData
    ) {
        if (structuredData == null
                || structuredData.isBlank()) {

            throw new IllegalStateException(
                    "Structured job description data is missing"
            );
        }

        return objectMapper.readValue(
                structuredData,
                StructuredJobDescription.class
        );
    }

    private List<RequirementToEvaluate> extractRequirements(
            StructuredJobDescription structuredJobDescription
    ) {

        List<RequirementToEvaluate> requirements =
                new ArrayList<>();

        requirements.addAll(
                toRequirements(
                        structuredJobDescription.requiredSkills(),
                        ClaimPriority.REQUIRED
                )
        );

        requirements.addAll(
                toRequirements(
                        structuredJobDescription.preferredSkills(),
                        ClaimPriority.PREFERRED
                )
        );

        requirements.addAll(
                toRequirements(
                        structuredJobDescription.requiredExperience(),
                        ClaimPriority.REQUIRED
                )
        );

        requirements.addAll(
                toRequirements(
                        structuredJobDescription.preferredExperience(),
                        ClaimPriority.PREFERRED
                )
        );

        requirements.addAll(
                toRequirements(
                        structuredJobDescription.requiredQualifications(),
                        ClaimPriority.REQUIRED
                )
        );

        requirements.addAll(
                toRequirements(
                        structuredJobDescription.preferredQualifications(),
                        ClaimPriority.PREFERRED
                )
        );

        return List.copyOf(requirements);
    }


    private List<RequirementToEvaluate> toRequirements(
            List<StructuredJobDescription.Requirement> requirements,
            ClaimPriority priority
    ) {

        if (requirements == null || requirements.isEmpty()) {
            return List.of();
        }

        return requirements.stream()
                .filter(Objects::nonNull)
                .map(requirement ->
                        new RequirementToEvaluate(
                                requirement,
                                priority
                        )
                )
                .toList();
    }
}
