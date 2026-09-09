package com.airesumeanalyzer.backend.ats.domain.model;

public record ATSScoreBreakdown(
        double score,
        int satisfiedCount,
        int totalCount
) {

    public ATSScoreBreakdown {

        if (Double.isNaN(score)
                || Double.isInfinite(score)
                || score < 0.0
                || score > 1.0) {

            throw new IllegalArgumentException(
                    "score must be between 0.0 and 1.0"
            );
        }

        if (satisfiedCount < 0) {
            throw new IllegalArgumentException(
                    "satisfiedCount must not be negative"
            );
        }

        if (totalCount < 0) {
            throw new IllegalArgumentException(
                    "totalCount must not be negative"
            );
        }

        if (satisfiedCount > totalCount) {
            throw new IllegalArgumentException(
                    "satisfiedCount must not exceed totalCount"
            );
        }
    }
}


