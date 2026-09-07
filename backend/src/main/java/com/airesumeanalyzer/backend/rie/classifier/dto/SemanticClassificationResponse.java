package com.airesumeanalyzer.backend.rie.classifier.dto;

import com.airesumeanalyzer.backend.rie.domain.MatchRelationship;

public record SemanticClassificationResponse(
        MatchRelationship relationship,
        String reasoning
) {
}