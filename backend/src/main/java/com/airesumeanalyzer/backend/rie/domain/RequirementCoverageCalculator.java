package com.airesumeanalyzer.backend.rie.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class RequirementCoverageCalculator {

    public RequirementCoverage calculate(
            RequirementExpression expression,
            List<RequirementMatch> matches
    ) {
        Objects.requireNonNull(
                expression,
                "expression must not be null"
        );

        Objects.requireNonNull(
                matches,
                "matches must not be null"
        );

        return calculateExpression(
                expression,
                matches
        );
    }

    private RequirementCoverage calculateExpression(
            RequirementExpression expression,
            List<RequirementMatch> matches
    ) {

        if (expression instanceof RequirementComponent component) {
            return calculateComponent(
                    component,
                    matches
            );
        }

        if (expression instanceof RequirementGroup group) {
            return calculateGroup(
                    group,
                    matches
            );
        }

        throw new IllegalStateException(
                "Unsupported requirement expression: "
                        + expression.getClass().getName()
        );
    }

    private RequirementCoverage calculateComponent(
            RequirementComponent component,
            List<RequirementMatch> matches
    ) {

        boolean matched =
                matches.stream()
                        .anyMatch(match ->
                                matchesComponent(
                                        match,
                                        component
                                )
                        );

        return new RequirementCoverage(
                1,
                matched ? 1 : 0
        );
    }

    private RequirementCoverage calculateGroup(
            RequirementGroup group,
            List<RequirementMatch> matches
    ) {

        if (group.operator() == RequirementOperator.ALL) {
            return calculateAllGroup(
                    group,
                    matches
            );
        }

        if (group.operator() == RequirementOperator.ANY) {
            return calculateAnyGroup(
                    group,
                    matches
            );
        }

        throw new IllegalStateException(
                "Unsupported requirement operator: "
                        + group.operator()
        );
    }

    private RequirementCoverage calculateAllGroup(
            RequirementGroup group,
            List<RequirementMatch> matches
    ) {

        int totalComponents = 0;
        int matchedComponents = 0;

        for (RequirementExpression expression :
                group.expressions()) {

            RequirementCoverage coverage =
                    calculateExpression(
                            expression,
                            matches
                    );

            totalComponents +=
                    coverage.totalComponents();

            matchedComponents +=
                    coverage.matchedComponents();
        }

        return new RequirementCoverage(
                totalComponents,
                matchedComponents
        );
    }

    private RequirementCoverage calculateAnyGroup(
            RequirementGroup group,
            List<RequirementMatch> matches
    ) {

        boolean satisfied =
                group.expressions()
                        .stream()
                        .anyMatch(expression ->
                                isSatisfied(
                                        expression,
                                        matches
                                )
                        );

        return new RequirementCoverage(
                1,
                satisfied ? 1 : 0
        );
    }

    private boolean isSatisfied(
            RequirementExpression expression,
            List<RequirementMatch> matches
    ) {

        RequirementCoverage coverage =
                calculateExpression(
                        expression,
                        matches
                );

        return coverage.totalComponents() > 0
                && coverage.totalComponents()
                == coverage.matchedComponents();
    }

    private boolean matchesComponent(
            RequirementMatch match,
            RequirementComponent component
    ) {

        if (match == null) {
            return false;
        }

        if (!match.requirementClaimId()
                .equals(component.requirementClaimId())) {
            return false;
        }

        if (!match.requirementComponentValue()
                .equalsIgnoreCase(component.value())) {
            return false;
        }

        return switch (match.relationship()) {
            case EXACT, EQUIVALENT, SATISFIES -> true;
            case RELATED, UNRELATED -> false;
        };
    }
}

