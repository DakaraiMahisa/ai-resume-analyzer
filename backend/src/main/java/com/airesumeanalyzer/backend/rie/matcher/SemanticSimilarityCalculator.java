package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;

public interface SemanticSimilarityCalculator {

    double calculate(
            Embedding left,
            Embedding right
    );
}
