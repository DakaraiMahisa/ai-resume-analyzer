package com.airesumeanalyzer.backend.ats.domain.model;

import java.util.List;
import java.util.Objects;

public record ATSResult(
        double overallScore,
        ATSScoreBreakdown required,
        ATSScoreBreakdown preferred,
        List<ATSRequirementScore> requirementScores
) {

    public ATSResult {

        if (Double.isNaN(overallScore)
                || Double.isInfinite(overallScore)
                || overallScore < 0.0
                || overallScore > 1.0) {

            throw new IllegalArgumentException(
                    "overallScore must be between 0.0 and 1.0"
            );
        }

        Objects.requireNonNull(
                required,
                "required breakdown must not be null"
        );

        Objects.requireNonNull(
                preferred,
                "preferred breakdown must not be null"
        );

        Objects.requireNonNull(
                requirementScores,
                "requirementScores must not be null"
        );

        if (requirementScores.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "requirementScores must not contain null elements"
            );
        }

        requirementScores = List.copyOf(requirementScores);
    }
}

