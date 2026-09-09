package com.airesumeanalyzer.backend.ats.domain.model;


import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;

import java.util.Objects;

public record ATSRequirementScore(
        RequirementMatchResult result,
        double score,
        boolean satisfied
) {

    public ATSRequirementScore {

        Objects.requireNonNull(
                result,
                "result must not be null"
        );

        if (Double.isNaN(score)
                || Double.isInfinite(score)
                || score < 0.0
                || score > 1.0) {

            throw new IllegalArgumentException(
                    "score must be between 0.0 and 1.0"
            );
        }
    }
}


