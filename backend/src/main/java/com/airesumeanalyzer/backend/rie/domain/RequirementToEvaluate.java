package com.airesumeanalyzer.backend.rie.domain;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;

import java.util.Objects;

public record RequirementToEvaluate(
        StructuredJobDescription.Requirement requirement,
        ClaimPriority priority
) {

    public RequirementToEvaluate {
        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                priority,
                "priority must not be null"
        );
    }
}