package com.airesumeanalyzer.backend.rie.domain;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record RequirementEvaluation(
        UUID requirementClaimId,
        RequirementExpression expression,
        List<RequirementMatch> matches,
        RequirementCoverage coverage
) {

    public RequirementEvaluation {
        Objects.requireNonNull(
                requirementClaimId,
                "requirementClaimId must not be null"
        );

        Objects.requireNonNull(
                expression,
                "expression must not be null"
        );

        Objects.requireNonNull(
                matches,
                "matches must not be null"
        );

        Objects.requireNonNull(
                coverage,
                "coverage must not be null"
        );

        if (matches.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "matches must not contain null elements"
            );
        }

        matches = List.copyOf(matches);
    }
}


