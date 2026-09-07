package com.airesumeanalyzer.backend.ats.domain.scoring;


import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ats.domain.model.ATSRequirementScore;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.ats.domain.model.ATSScoreBreakdown;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DefaultATSScoreAggregator
        implements ATSScoreAggregator {

    private static final double REQUIRED_WEIGHT = 0.70;
    private static final double PREFERRED_WEIGHT = 0.30;

    @Override
    public ATSResult aggregate(
            List<ATSRequirementScore> requirementScores
    ) {

        Objects.requireNonNull(
                requirementScores,
                "requirementScores must not be null"
        );

        if (requirementScores.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "requirementScores must not contain null elements"
            );
        }

        List<ATSRequirementScore> requiredScores =
                requirementScores.stream()
                        .filter(score ->
                                score.result().priority()
                                        == ClaimPriority.REQUIRED
                        )
                        .toList();

        List<ATSRequirementScore> preferredScores =
                requirementScores.stream()
                        .filter(score ->
                                score.result().priority()
                                        == ClaimPriority.PREFERRED
                        )
                        .toList();

        ATSScoreBreakdown required =
                calculateBreakdown(requiredScores);

        ATSScoreBreakdown preferred =
                calculateBreakdown(preferredScores);

        double overallScore =
                calculateOverallScore(
                        required,
                        preferred
                );

        return new ATSResult(
                overallScore,
                required,
                preferred,
                requirementScores
        );
    }

    private ATSScoreBreakdown calculateBreakdown(
            List<ATSRequirementScore> scores
    ) {

        if (scores.isEmpty()) {
            return new ATSScoreBreakdown(
                    0.0,
                    0,
                    0
            );
        }

        double totalScore =
                scores.stream()
                        .mapToDouble(ATSRequirementScore::score)
                        .sum();

        double averageScore =
                totalScore / scores.size();

        int satisfiedCount =
                (int) scores.stream()
                        .filter(ATSRequirementScore::satisfied)
                        .count();

        return new ATSScoreBreakdown(
                averageScore,
                satisfiedCount,
                scores.size()
        );
    }

    private double calculateOverallScore(
            ATSScoreBreakdown required,
            ATSScoreBreakdown preferred
    ) {

        boolean hasRequired =
                required.totalCount() > 0;

        boolean hasPreferred =
                preferred.totalCount() > 0;

        if (hasRequired && hasPreferred) {
            return
                    (required.score() * REQUIRED_WEIGHT)
                            + (preferred.score() * PREFERRED_WEIGHT);
        }

        if (hasRequired) {
            return required.score();
        }

        if (hasPreferred) {
            return preferred.score();
        }

        return 0.0;
    }
}

