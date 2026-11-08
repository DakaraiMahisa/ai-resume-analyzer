package com.airesumeanalyzer.backend.rie.capability;

import java.util.Objects;
import java.util.UUID;

public record SemanticMatchCandidate(
        UUID requirementClaimId,
        String requirementValue,
        UUID resumeClaimId,
        String resumeValue,
        double similarity
) {
    public SemanticMatchCandidate {
        Objects.requireNonNull(
                requirementClaimId,
                "requirementClaimId must not be null"
        );

        if (requirementValue == null
                || requirementValue.isBlank()) {
            throw new IllegalArgumentException(
                    "requirementValue must not be blank"
            );
        }

        Objects.requireNonNull(
                resumeClaimId,
                "resumeClaimId must not be null"
        );

        if (resumeValue == null
                || resumeValue.isBlank()) {
            throw new IllegalArgumentException(
                    "resumeValue must not be blank"
            );
        }

        if (!Double.isFinite(similarity)) {
            throw new IllegalArgumentException(
                    "similarity must be finite"
            );
        }

        requirementValue = requirementValue.trim();
        resumeValue = resumeValue.trim();
    }
}
