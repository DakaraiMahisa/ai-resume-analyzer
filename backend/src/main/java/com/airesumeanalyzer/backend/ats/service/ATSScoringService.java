package com.airesumeanalyzer.backend.ats.service;


import com.airesumeanalyzer.backend.ats.domain.model.ATSRequirementScore;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.ats.domain.scoring.ATSScoreAggregator;
import com.airesumeanalyzer.backend.ats.domain.scoring.RequirementScoreCalculator;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ATSScoringService {

    private final RequirementScoreCalculator requirementScoreCalculator;
    private final ATSScoreAggregator atsScoreAggregator;

    public ATSResult score(
            List<RequirementMatchResult> requirementResults
    ) {

        Objects.requireNonNull(
                requirementResults,
                "requirementResults must not be null"
        );

        if (requirementResults.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "requirementResults must not contain null elements"
            );
        }

        if (requirementResults.isEmpty()) {
            return atsScoreAggregator.aggregate(List.of());
        }

        List<ATSRequirementScore> requirementScores =
                requirementResults.stream()
                        .map(requirementScoreCalculator::calculate)
                        .toList();

        return atsScoreAggregator.aggregate(
                requirementScores
        );
    }
}

