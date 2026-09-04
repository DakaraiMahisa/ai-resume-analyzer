package com.airesumeanalyzer.backend.rie.domain;

import java.util.Objects;
import java.util.UUID;



public record RequirementMatch(
        UUID requirementClaimId,
        String requirementComponentValue,
        UUID resumeClaimId,
        MatchRelationship relationship,
        MatchingMethod method
) {

    public RequirementMatch {
        Objects.requireNonNull(
                requirementClaimId,
                "requirementClaimId must not be null"
        );

        if (requirementComponentValue == null
                || requirementComponentValue.isBlank()) {
            throw new IllegalArgumentException(
                    "requirementComponentValue must not be blank"
            );
        }

        requirementComponentValue =
                requirementComponentValue.trim();

        Objects.requireNonNull(
                resumeClaimId,
                "resumeClaimId must not be null"
        );

        Objects.requireNonNull(
                relationship,
                "relationship must not be null"
        );

        Objects.requireNonNull(
                method,
                "method must not be null"
        );
    }
}

