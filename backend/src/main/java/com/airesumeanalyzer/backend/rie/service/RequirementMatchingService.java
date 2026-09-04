package com.airesumeanalyzer.backend.rie.service;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.domain.RequirementCoverage;
import com.airesumeanalyzer.backend.rie.domain.RequirementCoverageCalculator;
import com.airesumeanalyzer.backend.rie.domain.RequirementEvaluation;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;
import com.airesumeanalyzer.backend.rie.matcher.RequirementMatcher;
import com.airesumeanalyzer.backend.rie.requirement.RequirementDecomposer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequirementMatchingService {

    private final RequirementMatcher requirementMatcher;
    private final RequirementDecomposer requirementDecomposer;
    private final RequirementCoverageCalculator coverageCalculator;

    public List<RequirementEvaluation> match(
            List<Claim> requirements,
            List<Claim> resumeClaims
    ) {
        Objects.requireNonNull(
                requirements,
                "requirements must not be null"
        );

        Objects.requireNonNull(
                resumeClaims,
                "resumeClaims must not be null"
        );

        if (requirements.isEmpty()) {
            return List.of();
        }

        List<RequirementEvaluation> evaluations =
                new ArrayList<>();

        for (Claim requirement : requirements) {

            if (requirement == null) {
                continue;
            }

            RequirementExpression expression =
                    Objects.requireNonNull(
                            requirementDecomposer.decompose(
                                    requirement
                            ),
                            "decomposer returned null expression"
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

            evaluations.add(
                    new RequirementEvaluation(
                            requirement.getId(),
                            expression,
                            matches,
                            coverage
                    )
            );
        }

        return List.copyOf(evaluations);
    }
}

