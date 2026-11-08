package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.rie.capability.SemanticMatchCandidate;
import com.airesumeanalyzer.backend.rie.domain.MatchRelationship;

import java.util.List;
import java.util.Map;

public interface SemanticBatchClassifier {

    Map<SemanticMatchCandidate, MatchRelationship> classifyBatch(
            List<SemanticMatchCandidate> candidates
    );
}
