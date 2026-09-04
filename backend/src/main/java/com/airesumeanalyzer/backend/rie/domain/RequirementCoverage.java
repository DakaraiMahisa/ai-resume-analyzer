package com.airesumeanalyzer.backend.rie.domain;

public record RequirementCoverage(
        int totalComponents,
        int matchedComponents
) {

    public RequirementCoverage {
        if (totalComponents < 0) {
            throw new IllegalArgumentException(
                    "totalComponents must not be negative"
            );
        }

        if (matchedComponents < 0) {
            throw new IllegalArgumentException(
                    "matchedComponents must not be negative"
            );
        }

        if (matchedComponents > totalComponents) {
            throw new IllegalArgumentException(
                    "matchedComponents must not exceed totalComponents"
            );
        }
    }

    public double coverage() {
        if (totalComponents == 0) {
            return 0.0;
        }

        return (double) matchedComponents / totalComponents;
    }
}


