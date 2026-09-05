package com.airesumeanalyzer.backend.rie.service;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.rie.domain.*;
import com.airesumeanalyzer.backend.rie.matcher.RequirementMatcher;
import com.airesumeanalyzer.backend.rie.requirement.StructuredRequirementExpressionBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequirementMatchingService {

    private final RequirementMatcher requirementMatcher;
    private final StructuredRequirementExpressionBuilder expressionBuilder;
    private final RequirementCoverageCalculator coverageCalculator;

    public List<RequirementMatchResult> match(
            List<RequirementToEvaluate> requirements,
            List<Claim> requirementClaims,
            List<Claim> resumeClaims
    ) {
        Objects.requireNonNull(
                requirements,
                "requirements must not be null"
        );

        Objects.requireNonNull(
                requirementClaims,
                "requirementClaims must not be null"
        );

        Objects.requireNonNull(
                resumeClaims,
                "resumeClaims must not be null"
        );

        if (requirements.isEmpty()) {
            return List.of();
        }

        List<RequirementMatchResult> evaluations =
                new ArrayList<>();

        for (RequirementToEvaluate requirementToEvaluate : requirements) {

            if (requirementToEvaluate == null) {
                continue;
            }

            StructuredJobDescription.Requirement requirement =
                    requirementToEvaluate.requirement();

            RequirementExpression expression =
                    Objects.requireNonNull(
                            expressionBuilder.build(
                                    requirement,
                                    requirementClaims
                            ),
                            "expression builder returned null expression"
                    );

            List<RequirementMatch> matches =
                    requirementMatcher.match(
                            expression,
                            resumeClaims
                    );

            RequirementCoverage coverage =
                    coverageCalculator.calculate(
                            expression,
                            matches
                    );

            RequirementEvaluation evaluation =
                    new RequirementEvaluation(
                            expression,
                            matches,
                            coverage
                    );

            evaluations.add(
                    new RequirementMatchResult(
                            requirement,
                            requirementToEvaluate.priority(),
                            evaluation
                    )
            );
        }

        return List.copyOf(evaluations);
    }
}


