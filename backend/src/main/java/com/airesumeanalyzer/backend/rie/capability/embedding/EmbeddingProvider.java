package com.airesumeanalyzer.backend.rie.capability.embedding;

import java.util.Collection;
import java.util.Map;

public interface EmbeddingProvider {

    Map<String, Embedding> embedBatch(Collection<String> values);
}