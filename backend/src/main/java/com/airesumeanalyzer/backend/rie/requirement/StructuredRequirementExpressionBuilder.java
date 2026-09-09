package com.airesumeanalyzer.backend.rie.requirement;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;
import com.airesumeanalyzer.backend.rie.domain.RequirementGroup;
import com.airesumeanalyzer.backend.rie.domain.RequirementOperator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class StructuredRequirementExpressionBuilder {

    public RequirementExpression build(
            StructuredJobDescription.Requirement requirement,
            List<Claim> requirementClaims
    ) {
        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                requirementClaims,
                "requirementClaims must not be null"
        );

        return buildExpression(
                requirement,
                requirementClaims
        );
    }

    private RequirementExpression buildExpression(
            StructuredJobDescription.Requirement requirement,
            List<Claim> requirementClaims
    ) {

        if (requirement.operator()
                == StructuredJobDescription.Operator.ATOMIC) {

            return buildAtomic(
                    requirement,
                    requirementClaims
            );
        }

        List<RequirementExpression> expressions =
                new ArrayList<>();

        if (hasValue(requirement)) {

            Claim claim =
                    findClaim(
                            requirement.value(),
                            requirementClaims
                    );

            expressions.add(
                    new RequirementComponent(
                            claim.getId(),
                            requirement.value()
                    )
            );
        }

        expressions.addAll(
                buildComponents(
                        requirement.components(),
                        requirementClaims
                )
        );

        if (expressions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Requirement must contain a value or components"
            );
        }

        return new RequirementGroup(
                toRequirementOperator(
                        requirement.operator()
                ),
                expressions
        );
    }

    private RequirementExpression buildAtomic(
            StructuredJobDescription.Requirement requirement,
            List<Claim> requirementClaims
    ) {

        if (!hasValue(requirement)) {
            throw new IllegalArgumentException(
                    "ATOMIC requirement must have a value"
            );
        }

        Claim claim =
                findClaim(
                        requirement.value(),
                        requirementClaims
                );

        return new RequirementComponent(
                claim.getId(),
                requirement.value()
        );
    }

    private List<RequirementExpression> buildComponents(
            List<StructuredJobDescription.Requirement> components,
            List<Claim> requirementClaims
    ) {

        if (components == null || components.isEmpty()) {
            return List.of();
        }

        return components.stream()
                .filter(Objects::nonNull)
                .map(component ->
                        buildExpression(
                                component,
                                requirementClaims
                        )
                )
                .toList();
    }

    private Claim findClaim(
            String value,
            List<Claim> requirementClaims
    ) {

        String normalizedValue = value.trim();

        return requirementClaims.stream()
                .filter(Objects::nonNull)
                .filter(claim ->
                        claim.getCanonicalName() != null
                                && claim.getCanonicalName()
                                .equalsIgnoreCase(
                                        normalizedValue
                                )
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No persisted requirement claim found for: "
                                        + value
                        )
                );
    }

    private boolean hasValue(
            StructuredJobDescription.Requirement requirement
    ) {
        return requirement.value() != null
                && !requirement.value().isBlank();
    }

    private RequirementOperator toRequirementOperator(
            StructuredJobDescription.Operator operator
    ) {

        return switch (operator) {
            case ALL -> RequirementOperator.ALL;
            case ANY -> RequirementOperator.ANY;
            case ATOMIC -> throw new IllegalArgumentException(
                    "ATOMIC cannot be converted to RequirementGroup"
            );
        };
    }
}

