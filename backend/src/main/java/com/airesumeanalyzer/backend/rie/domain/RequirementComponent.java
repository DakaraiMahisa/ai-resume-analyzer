package com.airesumeanalyzer.backend.rie.domain;

import java.util.Objects;
import java.util.UUID;

public record RequirementComponent(
        UUID requirementClaimId,
        String value
) implements RequirementExpression {

    public RequirementComponent {
        Objects.requireNonNull(
                requirementClaimId,
                "requirementClaimId must not be null"
        );

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "value must not be blank"
            );
        }

        value = value.trim();
    }
}
