package com.airesumeanalyzer.backend.ats.domain.scoring;

import com.airesumeanalyzer.backend.ats.domain.model.ATSRequirementScore;
import com.airesumeanalyzer.backend.rie.domain.*;

import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class DefaultRequirementScoreCalculator
        implements RequirementScoreCalculator {

    @Override
    public ATSRequirementScore calculate(
            RequirementMatchResult result
    ) {

        Objects.requireNonNull(
                result,
                "result must not be null"
        );

        RequirementCoverage coverage =
                Objects.requireNonNull(
                        result.evaluation().coverage(),
                        "coverage must not be null"
                );

        double score = calculateScore(coverage);

        boolean satisfied =
                isSatisfied(
                        result.evaluation().expression(),
                        coverage
                );

        return new ATSRequirementScore(
                result,
                score,
                satisfied
        );
    }

    private double calculateScore(
            RequirementCoverage coverage
    ) {

        if (coverage.totalComponents() == 0) {
            return 0.0;
        }

        return (double) coverage.matchedComponents()
                / coverage.totalComponents();
    }

    private boolean isSatisfied(
            RequirementExpression expression,
            RequirementCoverage coverage
    ) {

        Objects.requireNonNull(
                expression,
                "expression must not be null"
        );

        return switch (expression) {

            case RequirementComponent component ->
                    coverage.matchedComponents() > 0;

            case RequirementGroup group ->
                    switch (group.operator()) {

                        case ALL ->
                                coverage.matchedComponents()
                                        == coverage.totalComponents();

                        case ANY ->
                                coverage.matchedComponents() > 0;
                    };
        };
    }
}

