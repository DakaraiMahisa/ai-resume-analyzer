package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.rie.capability.SemanticMatchCandidate;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;

import java.util.List;
import java.util.Objects;

public record RequirementMatchingResult(
        List<RequirementMatch> matches,
        List<SemanticMatchCandidate> semanticCandidates
) {
    public RequirementMatchingResult {
        Objects.requireNonNull(
                matches,
                "matches must not be null"
        );

        Objects.requireNonNull(
                semanticCandidates,
                "semanticCandidates must not be null"
        );

        matches = List.copyOf(matches);
        semanticCandidates = List.copyOf(semanticCandidates);
    }
}