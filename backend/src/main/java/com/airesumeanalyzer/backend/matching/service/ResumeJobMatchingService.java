package com.airesumeanalyzer.backend.matching.service;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.entity.ClaimStatus;
import com.airesumeanalyzer.backend.ai.domain.repository.ClaimRepository;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;

import com.airesumeanalyzer.backend.rie.domain.RequirementEvaluation;
import com.airesumeanalyzer.backend.rie.service.RequirementMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeJobMatchingService {

    private final ClaimRepository claimRepository;
    private final RequirementMatchingService requirementMatchingService;

    @Transactional(readOnly = true)
    public List<RequirementEvaluation> match(
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

        List<Claim> jobDescriptionClaims =
                claimRepository.findBySourceDocumentIdAndStatus(
                        jobDescriptionId,
                        ClaimStatus.VALIDATED
                );
        List<Claim> requirements =
                selectRequirements(jobDescriptionClaims);

        return requirementMatchingService.match(
                requirements,
                resumeClaims
        );
    }


    private List<Claim> selectRequirements(
            List<Claim> jobDescriptionClaims
    ) {
        return jobDescriptionClaims.stream()
                .filter(claim ->
                        claim.getClaimType() == ClaimType.SKILL
                                || claim.getClaimType() == ClaimType.TECHNICAL_TOOL
                )
                .toList();
    }
}
