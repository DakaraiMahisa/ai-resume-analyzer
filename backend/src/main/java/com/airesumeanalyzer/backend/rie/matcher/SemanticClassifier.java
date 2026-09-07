package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.rie.domain.MatchRelationship;

public interface SemanticClassifier {

    MatchRelationship classify(
            String requirementValue,
            String resumeValue,
            double similarity
    );
}
