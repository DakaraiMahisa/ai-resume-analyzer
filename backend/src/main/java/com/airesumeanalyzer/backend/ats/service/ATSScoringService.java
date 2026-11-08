package com.airesumeanalyzer.backend.ats.service;


import com.airesumeanalyzer.backend.ats.domain.model.ATSRequirementScore;
import com.airesumeanalyzer.backend.ats.domain.model.ATSResult;
import com.airesumeanalyzer.backend.ats.domain.scoring.ATSScoreAggregator;
import com.airesumeanalyzer.backend.ats.domain.scoring.RequirementScoreCalculator;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatchResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
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
            log.warn("ATS scoring received no requirement results.");
            return atsScoreAggregator.aggregate(List.of());
        }

        List<ATSRequirementScore> requirementScores =
                requirementResults.stream()
                        .map(requirementScoreCalculator::calculate)
                        .toList();

        log.info(
                "ATS requirement scores calculated. resultCount={}, "
                        + "satisfiedCount={}, unsatisfiedCount={}",
                requirementScores.size(),
                requirementScores.stream()
                        .filter(ATSRequirementScore::satisfied)
                        .count(),
                requirementScores.stream()
                        .filter(score -> !score.satisfied())
                        .count()
        );

        requirementScores.forEach(score -> log.debug(
                "ATS requirement score. priority={}, score={}, satisfied={}, "
                        + "coverage={}/{}",
                score.result().priority(),
                score.score(),
                score.satisfied(),
                score.result().evaluation().coverage().matchedComponents(),
                score.result().evaluation().coverage().totalComponents()
        ));

        ATSResult result = atsScoreAggregator.aggregate(
                requirementScores
        );
        log.info(
                "ATS score aggregated. overallScore={}, requiredScore={}, "
                        + "preferredScore={}",
                result.overallScore(),
                result.required().score(),
                result.preferred().score()
        );
        return result;
    }
}
