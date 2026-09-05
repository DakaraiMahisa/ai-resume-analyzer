package com.airesumeanalyzer.backend.rie.domain;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;

import java.util.Objects;

public record RequirementMatchResult(
        StructuredJobDescription.Requirement requirement,
        ClaimPriority priority,
        RequirementEvaluation evaluation
) {

    public RequirementMatchResult {
        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                priority,
                "priority must not be null"
        );

        Objects.requireNonNull(
                evaluation,
                "evaluation must not be null"
        );
    }
}